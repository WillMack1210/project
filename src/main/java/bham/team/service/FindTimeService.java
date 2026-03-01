package bham.team.service;

import bham.team.domain.Event;
import bham.team.repository.EventRepository;
import bham.team.service.schedule.TimeSlot;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FindTimeService {

    private final EventRepository eventRepository;
    private static final Duration minSlot = Duration.ofMinutes(60);

    public FindTimeService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    private List<Event> getAllEvents(Long userId, Long friendId, Instant start, Instant end) {
        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(eventRepository.findByUserProfileIdAndTimeRange(userId, start, end));
        allEvents.addAll(eventRepository.findByUserProfileIdAndTimeRange(friendId, start, end));
        return allEvents;
    }

    public List<TimeSlot> computeFreeSlots(Long userId, Long friendId, Instant start, Instant end) {
        List<TimeSlot> freeSlots = new ArrayList<>();
        List<Event> busyEvents = getAllEvents(userId, friendId, start, end);
        if (busyEvents.isEmpty()) {
            freeSlots.add(new TimeSlot(start, end));
            return freeSlots;
        } else {
            busyEvents = mergeEvents(busyEvents);
            Instant cursor = start;
            for (Event event : busyEvents) {
                Instant eventStart = event.getStartTime();
                Instant eventEnd = event.getEndTime();
                if (eventStart.isAfter(cursor)) {
                    Duration gap = Duration.between(cursor, eventStart);
                    if (!gap.minus(minSlot).isNegative()) {
                        freeSlots.add(new TimeSlot(cursor, eventStart));
                    }
                }
                if (eventEnd.isAfter(cursor)) {
                    cursor = eventEnd;
                }
            }
            if (cursor.isBefore(end)) {
                freeSlots.add(new TimeSlot(cursor, end));
            }
            return freeSlots;
        }
    }

    private List<Event> mergeEvents(List<Event> events) {
        if (events.isEmpty()) return events;
        events.sort(Comparator.comparing(Event::getStartTime));
        List<Event> merged = new ArrayList<>();
        Event current = events.get(0);
        for (int i = 1; i < events.size(); i++) {
            Event next = events.get(i);
            if (!next.getStartTime().isAfter(current.getEndTime())) {
                if (next.getEndTime().isAfter(current.getEndTime())) {
                    current.setEndTime(next.getEndTime());
                }
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }
}
