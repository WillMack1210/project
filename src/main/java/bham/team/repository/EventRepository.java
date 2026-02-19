package bham.team.repository;

import bham.team.domain.Event;
import bham.team.domain.UserProfile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Event entity.
 *
 * When extending this class, extend EventRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface EventRepository extends EventRepositoryWithBagRelationships, JpaRepository<Event, Long> {
    default Optional<Event> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Event> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Event> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    default List<Event> findByOwnerID(Long ownerId) {
        return this.fetchBagRelationships(this.findByOwnerID(ownerId));
    }

    List<Event> findAllByOwnerAndStartTimeLessThanAndEndTimeGreaterThan(UserProfile owner, Instant windowEnd, Instant windowStart);

    @Query("select e from Event e where e.owner.id = :profileId")
    List<Event> findByUserProfileId(@Param("profileId") Long profileId);
}
