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

    @Query(
        """
        SELECT f FROM Friendship f WHERE
        (f.requester.id = :userId1 AND f.addressee.id = :userId2)
        OR (f.requester.id = :userId2 AND f.addressee.id = :userId1) """
    )
    Optional<Friendship> findBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(
        """
         SELECT f FROM Friendship f WHERE
        (f.requester.id = :userId OR f.addressee.id = :userId)
        AND f.status = bham.team.domain.enumeration.FriendStatus.ACCEPTED
         """
    )
    List<Friendship> findAcceptedForUser(Long userId);

    @Query(
        """
         SELECT f FROM Friendship f WHERE
        (f.requester.id = :userId OR f.addressee.id = :userId)
        AND f.status = bham.team.domain.enumeration.FriendStatus.PENDING
         """
    )
    List<Friendship> findRequestedForUser(Long userId);
}
