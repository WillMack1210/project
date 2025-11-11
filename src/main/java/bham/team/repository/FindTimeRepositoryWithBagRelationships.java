package bham.team.repository;

import bham.team.domain.FindTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface FindTimeRepositoryWithBagRelationships {
    Optional<FindTime> fetchBagRelationships(Optional<FindTime> findTime);

    List<FindTime> fetchBagRelationships(List<FindTime> findTimes);

    Page<FindTime> fetchBagRelationships(Page<FindTime> findTimes);
}
