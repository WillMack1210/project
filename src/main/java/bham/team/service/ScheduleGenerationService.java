package bham.team.service;

import bham.team.domain.Event;
import bham.team.domain.ScheduleRequest;
import bham.team.domain.UserProfile;
import bham.team.domain.enumeration.PrivacyStatus;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleGenerationService {

    private final EventRepository eventRepository;
    private final ScheduleRequestRepository scheduleRequestRepository;
    private static final LocalTime LATEST_END_TIME = LocalTime.of(20, 30);
    private static final LocalTime EARLIEST_START_TIME = LocalTime.of(8, 30);

    public ScheduleGenerationService(EventRepository eventRepository, ScheduleRequestRepository scheduleRequestRepository) {
        this.eventRepository = eventRepository;
        this.scheduleRequestRepository = scheduleRequestRepository;
    }

    public List<Event> generate(Long requestId) {
        ScheduleRequest request = scheduleRequestRepository.findById(requestId).orElseThrow();
        UserProfile user = request.getUser();
        List<ActivityTemplate> templates = parseDesciription(request.getScheduleDescription());
        List<PlannedEvent> plannedEvents = flatten(templates);

        List<Event> busyEvents = eventRepository.findAllByOwnerAndStartTimeLessThanAndEndTimeGreaterThan(
            user,
            request.getEndDate(),
            request.getStartDate()
        );

        ZoneId zone = ZoneId.systemDefault();
        List<TimeSlot> rawSlots = computeFreeSlots(request.getStartDate(), request.getEndDate(), busyEvents);
        List<TimeSlot> freeSlots = new ArrayList<>();
        for (TimeSlot slot : rawSlots) {
            freeSlots.addAll(splitSlotByDay(slot, zone));
        }
        return placeEvents(plannedEvents, freeSlots, user);
    }

    private List<ActivityTemplate> parseDesciription(String description) {
        List<ActivityTemplate> results = new ArrayList<>();
        String[] lines = description.split("\\R|_");
        Pattern p = Pattern.compile("(.+?)\\s+(?:(\\d+)\\s*h\\s*)?(?:(\\d+)\\s*m\\s*)?x\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        for (String line : lines) {
            Matcher m = p.matcher(line.trim());
            if (!m.matches()) {
                continue; // or throw validation error
            }
            String title = m.group(1).trim();
            int hours = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
            long minutes = m.group(3) != null ? Long.parseLong(m.group(3)) : 0;
            int count = Integer.parseInt(m.group(4));
            Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
            results.add(new ActivityTemplate(title, duration, count));
        }
        return results;
    }

    private List<PlannedEvent> flatten(List<ActivityTemplate> templates) {
        List<PlannedEvent> planned = new ArrayList<>();
        for (ActivityTemplate t : templates) {
            for (int i = 0; i < t.count(); i++) {
                planned.add(new PlannedEvent(t.title(), t.duration()));
            }
        }
        return planned;
    }

    private List<Event> placeEvents(List<PlannedEvent> planned, List<TimeSlot> freeSlots, UserProfile user) {
        List<Event> created = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        Map<LocalDate, Set<String>> eventsPerDay = new HashMap<>();
        for (PlannedEvent p : planned) {
            for (TimeSlot slot : freeSlots) {
                if (slot.length().isZero() || slot.length().isNegative()) {
                    continue;
                }
                if (slot.length().compareTo(p.duration()) < 0) {
                    continue;
                }
                Instant potentialStart = slot.getStart();
                if (tooEarly(potentialStart, zone)) {
                    continue;
                }
                if (tooLate(potentialStart, p.duration(), zone)) {
                    continue;
                }
                LocalDate day = potentialStart.atZone(zone).toLocalDate();
                eventsPerDay.computeIfAbsent(day, d -> new HashSet<>());
                if (eventsPerDay.get(day).contains(p.title())) {
                    continue;
                }
                Event e = new Event();
                e.setTitle(p.title());
                e.setOwner(user);
                e.setStartTime(potentialStart);
                e.setEndTime(potentialStart.plus(p.duration()));
                e.setPrivacy(PrivacyStatus.PRIVATE);
                e.setDescription("Generated from webpage");
                created.add(eventRepository.save(e));
                slot.consume(p.duration());
                eventsPerDay.get(day).add(p.title());
                break;
            }
        }
        return created;
    }

    private List<TimeSlot> computeFreeSlots(Instant rangeStart, Instant rangeEnd, List<Event> busyEvents) {
        List<TimeSlot> freeSlots = new ArrayList<>();
        busyEvents.sort(Comparator.comparing(Event::getStartTime));
        Instant cursor = rangeStart;
        for (Event e : busyEvents) {
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
        boolean early = startTime.isBefore(EARLIEST_START_TIME);
        return early;
    }

    private boolean tooLate(Instant start, Duration length, ZoneId zone) {
        LocalTime endTime = start.plus(length).atZone(zone).toLocalTime();
        boolean late = endTime.isAfter(LATEST_END_TIME);
        return late;
    }
}
