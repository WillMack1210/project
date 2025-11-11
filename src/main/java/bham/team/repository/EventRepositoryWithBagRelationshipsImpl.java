package bham.team.repository;

import bham.team.domain.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class EventRepositoryWithBagRelationshipsImpl implements EventRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String EVENTS_PARAMETER = "events";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Event> fetchBagRelationships(Optional<Event> event) {
        return event.map(this::fetchParticipants);
    }

    @Override
    public Page<Event> fetchBagRelationships(Page<Event> events) {
        return new PageImpl<>(fetchBagRelationships(events.getContent()), events.getPageable(), events.getTotalElements());
    }

    @Override
    public List<Event> fetchBagRelationships(List<Event> events) {
        return Optional.of(events).map(this::fetchParticipants).orElse(Collections.emptyList());
    }

    Event fetchParticipants(Event result) {
        return entityManager
            .createQuery("select event from Event event left join fetch event.participants where event.id = :id", Event.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Event> fetchParticipants(List<Event> events) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, events.size()).forEach(index -> order.put(events.get(index).getId(), index));
        List<Event> result = entityManager
            .createQuery("select event from Event event left join fetch event.participants where event in :events", Event.class)
            .setParameter(EVENTS_PARAMETER, events)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
