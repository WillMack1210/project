package bham.team.repository;

import bham.team.domain.FindTime;
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
public class FindTimeRepositoryWithBagRelationshipsImpl implements FindTimeRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String FINDTIMES_PARAMETER = "findTimes";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<FindTime> fetchBagRelationships(Optional<FindTime> findTime) {
        return findTime.map(this::fetchParticipants);
    }

    @Override
    public Page<FindTime> fetchBagRelationships(Page<FindTime> findTimes) {
        return new PageImpl<>(fetchBagRelationships(findTimes.getContent()), findTimes.getPageable(), findTimes.getTotalElements());
    }

    @Override
    public List<FindTime> fetchBagRelationships(List<FindTime> findTimes) {
        return Optional.of(findTimes).map(this::fetchParticipants).orElse(Collections.emptyList());
    }

    FindTime fetchParticipants(FindTime result) {
        return entityManager
            .createQuery(
                "select findTime from FindTime findTime left join fetch findTime.participants where findTime.id = :id",
                FindTime.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<FindTime> fetchParticipants(List<FindTime> findTimes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, findTimes.size()).forEach(index -> order.put(findTimes.get(index).getId(), index));
        List<FindTime> result = entityManager
            .createQuery(
                "select findTime from FindTime findTime left join fetch findTime.participants where findTime in :findTimes",
                FindTime.class
            )
            .setParameter(FINDTIMES_PARAMETER, findTimes)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
