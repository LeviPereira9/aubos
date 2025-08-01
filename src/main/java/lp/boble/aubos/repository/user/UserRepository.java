package lp.boble.aubos.repository.user;

import lp.boble.aubos.dto.user.UserAutocompletePageProjection;
import lp.boble.aubos.dto.user.UserSuggestionPageProjection;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    @EntityGraph(attributePaths = {"role", "status"})
    @Query("""
        SELECT u FROM UserModel u
        WHERE u.status.name = "ACTIVE" AND u.username = :username
""")
    Optional<UserModel> findByUsername(String username);

    @EntityGraph(attributePaths = {"role", "status"})
    Optional<UserModel> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    @Query("""
       SELECT
        u.username as username,
        u.displayName as displayName,
        u.isVerified as isVerified,
        u.isOfficial as isOfficial,
        u.profilePic as profilePic
       FROM UserModel u
           WHERE
               u.status.name = 'ACTIVE'
               AND
               (u.username LIKE CONCAT(:query, "%")
               OR
               u.displayName LIKE CONCAT('%', REPLACE(:query, ' ', '%'), '%'))
       ORDER BY u.displayName
""")
    Page<UserAutocompletePageProjection> findUserAutocomplete(
          @Param("query") String query,
          Pageable pageable);


    @Query(
            """
        SELECT
            u.username as username,
            u.displayName as displayName,
            u.profilePic as profilePic,
            u.bio as bio,
            u.isVerified as isVerified,
            u.isOfficial as isOfficial
           FROM UserModel u
               WHERE
                   u.status.name = 'ACTIVE'
                   AND
                   (u.username LIKE CONCAT(:query, "%")
                   OR
                   u.displayName LIKE CONCAT('%', REPLACE(:query, ' ', '%'), '%'))
           ORDER BY u.displayName
"""
    )
    Page<UserSuggestionPageProjection> findUserSuggestions(
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
    SELECT u.updatedAt FROM UserModel u
    WHERE u.status.name = 'ACTIVE' AND u.username = :username
""")
    Optional<Instant> getUpdate(String username);
}
