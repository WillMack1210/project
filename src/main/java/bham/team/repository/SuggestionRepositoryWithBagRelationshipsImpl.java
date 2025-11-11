package bham.team.repository;

import bham.team.domain.Suggestion;
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
public class SuggestionRepositoryWithBagRelationshipsImpl implements SuggestionRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String SUGGESTIONS_PARAMETER = "suggestions";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Suggestion> fetchBagRelationships(Optional<Suggestion> suggestion) {
        return suggestion.map(this::fetchFindTimes);
    }

    @Override
    public Page<Suggestion> fetchBagRelationships(Page<Suggestion> suggestions) {
        return new PageImpl<>(fetchBagRelationships(suggestions.getContent()), suggestions.getPageable(), suggestions.getTotalElements());
    }

    @Override
    public List<Suggestion> fetchBagRelationships(List<Suggestion> suggestions) {
        return Optional.of(suggestions).map(this::fetchFindTimes).orElse(Collections.emptyList());
    }

    Suggestion fetchFindTimes(Suggestion result) {
        return entityManager
            .createQuery(
                "select suggestion from Suggestion suggestion left join fetch suggestion.findTimes where suggestion.id = :id",
                Suggestion.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Suggestion> fetchFindTimes(List<Suggestion> suggestions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, suggestions.size()).forEach(index -> order.put(suggestions.get(index).getId(), index));
        List<Suggestion> result = entityManager
            .createQuery(
                "select suggestion from Suggestion suggestion left join fetch suggestion.findTimes where suggestion in :suggestions",
                Suggestion.class
            )
            .setParameter(SUGGESTIONS_PARAMETER, suggestions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
