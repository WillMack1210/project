package bham.team.repository;

import bham.team.domain.Suggestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SuggestionRepositoryWithBagRelationships {
    Optional<Suggestion> fetchBagRelationships(Optional<Suggestion> suggestion);

    List<Suggestion> fetchBagRelationships(List<Suggestion> suggestions);

    Page<Suggestion> fetchBagRelationships(Page<Suggestion> suggestions);
}
