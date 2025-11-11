package bham.team.repository;

import bham.team.domain.ScheduleRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScheduleRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequest, Long> {}
