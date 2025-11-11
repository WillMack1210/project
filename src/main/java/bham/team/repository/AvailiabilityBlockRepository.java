package bham.team.repository;

import bham.team.domain.AvailiabilityBlock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AvailiabilityBlock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AvailiabilityBlockRepository extends JpaRepository<AvailiabilityBlock, Long> {}
