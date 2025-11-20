package bham.team.repository;

import bham.team.domain.Friendship;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    default Optional<Friendship> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Friendship> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Friendship> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select friendship from Friendship friendship left join fetch friendship.requester left join fetch friendship.addressee",
        countQuery = "select count(friendship) from Friendship friendship"
    )
    Page<Friendship> findAllWithToOneRelationships(Pageable pageable);

    @Query("select friendship from Friendship friendship left join fetch friendship.requester left join fetch friendship.addressee")
    List<Friendship> findAllWithToOneRelationships();

    @Query(
        "select friendship from Friendship friendship left join fetch friendship.requester left join fetch friendship.addressee where friendship.id =:id"
    )
    Optional<Friendship> findOneWithToOneRelationships(@Param("id") Long id);
}
