package bham.team.service;

import bham.team.domain.Event;
import bham.team.domain.ScheduleRequest;
import bham.team.domain.UserProfile;
import bham.team.domain.enumeration.PrivacyStatus;
import bham.team.domain.enumeration.ScheduleIntensity;
import bham.team.repository.EventRepository;
import bham.team.repository.ScheduleRequestRepository;
import bham.team.service.schedule.ActivityTemplate;
import bham.team.service.schedule.PlannedEvent;
import bham.team.service.schedule.TimeSlot;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleGenerationService {

    private final EventRepository eventRepository;
    private final ScheduleRequestRepository scheduleRequestRepository;

    private static final LocalTime LATEST_END_TIME = LocalTime.of(22, 00);
    private static final LocalTime EARLIEST_START_TIME = LocalTime.of(8, 30);

    public ScheduleGenerationService(EventRepository eventRepository, ScheduleRequestRepository scheduleRequestRepository) {
        this.eventRepository = eventRepository;
        this.scheduleRequestRepository = scheduleRequestRepository;
    }

    private static class CandidatePlacement {

        private final TimeSlot slot;
        private final Instant start;
        private final double score;

        private CandidatePlacement(TimeSlot slot, Instant start, double score) {
            this.slot = slot;
            this.start = start;
            this.score = score;
        }

        public double getScore() {
            return score;
        }
    }

    public List<Event> generate(Long requestId) {
        ScheduleRequest request = scheduleRequestRepository.findById(requestId).orElseThrow();
        UserProfile user = request.getUser();

        List<ActivityTemplate> templates = parseDescription(request.getScheduleDescription());
        List<PlannedEvent> plannedEvents = flattenAndPrioritise(templates);

        List<Event> busyEvents = eventRepository.findBusyEventsForOwnerInWindow(user, request.getEndDate(), request.getStartDate());

        ZoneId zone = ZoneId.systemDefault();
        List<TimeSlot> rawSlots = computeFreeSlots(request.getStartDate(), request.getEndDate(), busyEvents);
        List<TimeSlot> freeSlots = new ArrayList<>();

        for (TimeSlot slot : rawSlots) {
            freeSlots.addAll(splitSlotByDay(slot, zone));
        }

        Random random = new Random(requestId);

        return placeEventsSmart(plannedEvents, freeSlots, user, request.getIntensity(), request.getPrivacy(), random, zone);
    }

    private List<ActivityTemplate> parseDescription(String description) {
        List<ActivityTemplate> results = new ArrayList<>();
        String[] lines = description.split("\\R|_");

        Pattern p = Pattern.compile("(.+?)\\s+(?:(\\d+)\\s*h\\s*)?(?:(\\d+)\\s*m\\s*)?x\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            Matcher m = p.matcher(line);
            if (!m.matches()) {
                continue;
            }

            String title = m.group(1).trim();
            int hours = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
            int minutes = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            int count = Integer.parseInt(m.group(4));

            Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
            if (duration.isZero() || duration.isNegative() || count <= 0) {
                continue;
            }

            results.add(new ActivityTemplate(title, duration, count));
        }

        return results;
    }

    private List<PlannedEvent> flattenAndPrioritise(List<ActivityTemplate> templates) {
        List<PlannedEvent> planned = new ArrayList<>();

        for (ActivityTemplate t : templates) {
            for (int i = 0; i < t.count(); i++) {
                planned.add(new PlannedEvent(t.title(), t.duration()));
            }
        }

        planned.sort((a, b) -> {
            int durationCompare = b.duration().compareTo(a.duration());
            if (durationCompare != 0) {
                return durationCompare;
            }
            return a.title().compareToIgnoreCase(b.title());
        });

        return planned;
    }

    private List<Event> placeEventsSmart(
        List<PlannedEvent> planned,
        List<TimeSlot> freeSlots,
        UserProfile user,
        ScheduleIntensity intensity,
        PrivacyStatus privacy,
        Random random,
        ZoneId zone
    ) {
        List<Event> created = new ArrayList<>();

        Map<LocalDate, Integer> loadPerDay = new HashMap<>();
        Map<String, List<LocalDate>> daysUsedByTitle = new HashMap<>();

        Duration gap =
            switch (intensity) {
                case EASY -> Duration.ofHours(2);
                case INTERMEDIATE -> Duration.ofHours(1);
                case INTENSE -> Duration.ofMinutes(30);
            };

        for (PlannedEvent plannedEvent : planned) {
            List<CandidatePlacement> candidates = buildCandidates(plannedEvent, freeSlots, gap, zone, loadPerDay, daysUsedByTitle);

            if (candidates.isEmpty()) {
                continue;
            }

            candidates.sort(Comparator.comparingDouble(CandidatePlacement::getScore).reversed());

            int topCount = Math.min(3, candidates.size());
            CandidatePlacement chosen = candidates.get(random.nextInt(topCount));

            Event event = new Event();
            event.setTitle(plannedEvent.title());
            event.setOwner(user);
            event.setStartTime(chosen.start);
            event.setEndTime(chosen.start.plus(plannedEvent.duration()));
            event.setPrivacy(privacy);
            event.setDescription("Generated from webpage");

            created.add(eventRepository.save(event));

            updateFreeSlots(freeSlots, chosen.slot, chosen.start, plannedEvent.duration(), gap);
            LocalDate day = chosen.start.atZone(zone).toLocalDate();
            loadPerDay.merge(day, 1, Integer::sum);
            daysUsedByTitle.computeIfAbsent(plannedEvent.title(), k -> new ArrayList<>()).add(day);
        }

        return created;
    }

    private List<CandidatePlacement> buildCandidates(
        PlannedEvent plannedEvent,
        List<TimeSlot> freeSlots,
        Duration gap,
        ZoneId zone,
        Map<LocalDate, Integer> loadPerDay,
        Map<String, List<LocalDate>> daysUsedByTitle
    ) {
        List<CandidatePlacement> candidates = new ArrayList<>();

        for (TimeSlot slot : freeSlots) {
            Instant windowStart = slot.getStart();
            Instant windowEnd = slot.getEnd();

            if (windowEnd.isBefore(windowStart.plus(plannedEvent.duration()))) {
                continue;
            }

            for (Instant candidateStart : generateStartTimes(windowStart, windowEnd, plannedEvent.duration(), zone)) {
                if (tooEarly(candidateStart, zone) || tooLate(candidateStart, plannedEvent.duration(), zone)) {
                    continue;
                }

                double score = scorePlacement(plannedEvent, candidateStart, slot, zone, loadPerDay, daysUsedByTitle);

                candidates.add(new CandidatePlacement(slot, candidateStart, score));
            }
        }

        return candidates;
    }

    private double scorePlacement(
        PlannedEvent plannedEvent,
        Instant start,
        TimeSlot slot,
        ZoneId zone,
        Map<LocalDate, Integer> loadPerDay,
        Map<String, List<LocalDate>> daysUsedByTitle
    ) {
        double score = 0.0;

        LocalDate day = start.atZone(zone).toLocalDate();
        LocalTime time = start.atZone(zone).toLocalTime();

        score += preferredTimeScore(plannedEvent.title(), time);

        List<LocalDate> usedDays = daysUsedByTitle.getOrDefault(plannedEvent.title(), List.of());
        if (!usedDays.contains(day)) {
            score += 15.0;
        } else {
            score -= 25.0;
        }

        int currentLoad = loadPerDay.getOrDefault(day, 0);
        score -= currentLoad * 6.0;

        Duration leftover = Duration.between(start.plus(plannedEvent.duration()), slot.getEnd());
        long leftoverMinutes = Math.max(0, leftover.toMinutes());

        if (leftoverMinutes >= 30 && leftoverMinutes <= 90) {
            score += 4.0;
        } else if (leftoverMinutes < 20) {
            score -= 2.0;
        }

        score += Math.max(0, 10 - (time.getHour() - 9));

        if (isWeekend(day) && isWorkLike(plannedEvent.title())) {
            score -= 8.0;
        }

        return score;
    }

    private List<Instant> generateStartTimes(Instant start, Instant end, Duration duration, ZoneId zone) {
        List<Instant> starts = new ArrayList<>();
        Instant candidate = roundUpToNextHalfHour(start, zone);

        while (!candidate.plus(duration).isAfter(end)) {
            starts.add(candidate);
            candidate = candidate.plus(Duration.ofMinutes(30));
        }

        return starts;
    }

    private Instant roundUpToNextHalfHour(Instant instant, ZoneId zone) {
        var zdt = instant.atZone(zone).withSecond(0).withNano(0);
        int minute = zdt.getMinute();

        if (minute == 0 || minute == 30) {
            return zdt.toInstant();
        }

        if (minute < 30) {
            zdt = zdt.withMinute(30);
        } else {
            zdt = zdt.plusHours(1).withMinute(0);
        }

        return zdt.toInstant();
    }

    private List<TimeSlot> computeFreeSlots(Instant rangeStart, Instant rangeEnd, List<Event> busyEvents) {
        List<TimeSlot> freeSlots = new ArrayList<>();

        List<Event> blockingEvents = busyEvents
            .stream()
            .filter(e -> e.getEndTime() != null)
            .sorted(Comparator.comparing(Event::getStartTime))
            .toList();

        Instant cursor = rangeStart;
        for (Event e : blockingEvents) {
            if (e.getStartTime().isAfter(cursor)) {
                freeSlots.add(new TimeSlot(cursor, e.getStartTime()));
            }
            if (e.getEndTime().isAfter(cursor)) {
                cursor = e.getEndTime();
            }
        }

        if (cursor.isBefore(rangeEnd)) {
            freeSlots.add(new TimeSlot(cursor, rangeEnd));
        }

        return freeSlots;
    }

    private List<TimeSlot> splitSlotByDay(TimeSlot slot, ZoneId zone) {
        List<TimeSlot> result = new ArrayList<>();
        Instant start = slot.getStart();
        Instant end = slot.getEnd();

        while (start.isBefore(end)) {
            LocalDate day = start.atZone(zone).toLocalDate();
            Instant dayStart = day.atTime(EARLIEST_START_TIME).atZone(zone).toInstant();
            Instant dayEnd = day.atTime(LATEST_END_TIME).atZone(zone).toInstant();

            Instant slotStart = start.isBefore(dayStart) ? dayStart : start;
            Instant slotEnd = end.isBefore(dayEnd) ? end : dayEnd;

            if (slotStart.isBefore(slotEnd)) {
                result.add(new TimeSlot(slotStart, slotEnd));
            }

            start = day.plusDays(1).atStartOfDay(zone).toInstant();
        }

        return result;
    }

    private boolean tooEarly(Instant start, ZoneId zone) {
        LocalTime startTime = start.atZone(zone).toLocalTime();
        return startTime.isBefore(EARLIEST_START_TIME);
    }

    private boolean tooLate(Instant start, Duration length, ZoneId zone) {
        LocalTime endTime = start.plus(length).atZone(zone).toLocalTime();
        return endTime.isAfter(LATEST_END_TIME);
    }

    private double preferredTimeScore(String title, LocalTime time) {
        String t = title.toLowerCase();

        if (t.contains("study") || t.contains("revision") || t.contains("coding") || t.contains("work")) {
            return closenessToWindow(time, LocalTime.of(9, 0), LocalTime.of(13, 0));
        }

        if (t.contains("gym") || t.contains("run") || t.contains("workout") || t.contains("exercise")) {
            return closenessToWindow(time, LocalTime.of(16, 0), LocalTime.of(19, 30));
        }

        if (t.contains("read") || t.contains("reading") || t.contains("journal") || t.contains("meditation")) {
            return closenessToWindow(time, LocalTime.of(18, 0), LocalTime.of(20, 0));
        }

        return closenessToWindow(time, LocalTime.of(10, 0), LocalTime.of(17, 0));
    }

    private double closenessToWindow(LocalTime time, LocalTime preferredStart, LocalTime preferredEnd) {
        if (!time.isBefore(preferredStart) && !time.isAfter(preferredEnd)) {
            return 20.0;
        }

        long minutesFromStart = Math.abs(Duration.between(preferredStart, time).toMinutes());
        long minutesFromEnd = Math.abs(Duration.between(preferredEnd, time).toMinutes());
        long distance = Math.min(minutesFromStart, minutesFromEnd);

        return Math.max(-10.0, 20.0 - (distance / 15.0));
    }

    private boolean isWeekend(LocalDate day) {
        return day.getDayOfWeek().getValue() >= 6;
    }

    private boolean isWorkLike(String title) {
        String t = title.toLowerCase();
        return t.contains("study") || t.contains("revision") || t.contains("work") || t.contains("coding");
    }

    private void updateFreeSlots(List<TimeSlot> freeSlots, TimeSlot chosenSlot, Instant start, Duration duration, Duration gap) {
        Instant blockedStart = start.minus(gap);
        Instant blockedEnd = start.plus(duration).plus(gap);

        if (blockedStart.isBefore(chosenSlot.getStart())) {
            blockedStart = chosenSlot.getStart();
        }

        if (blockedEnd.isAfter(chosenSlot.getEnd())) {
            blockedEnd = chosenSlot.getEnd();
        }

        freeSlots.remove(chosenSlot);

        if (chosenSlot.getStart().isBefore(blockedStart)) {
            freeSlots.add(new TimeSlot(chosenSlot.getStart(), blockedStart));
        }

        if (blockedEnd.isBefore(chosenSlot.getEnd())) {
            freeSlots.add(new TimeSlot(blockedEnd, chosenSlot.getEnd()));
        }

        freeSlots.sort(Comparator.comparing(TimeSlot::getStart));
    }
}
