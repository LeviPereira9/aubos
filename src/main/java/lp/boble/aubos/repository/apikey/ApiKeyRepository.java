package lp.boble.aubos.repository.apikey;

import lp.boble.aubos.model.apikey.ApiKeyModel;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKeyModel, UUID> {

    @EntityGraph(attributePaths = {"owner"})
    Optional<ApiKeyModel> findByPublicId(String publicId);

    @EntityGraph(attributePaths = {"owner"})
    Optional<ApiKeyModel> findByPublicIdAndOwnerUsername(String publicId, String username);

    @Query("""
    SELECT k FROM ApiKeyModel k
    WHERE k.owner = :owner AND k.softDelete = false
""")
    List<ApiKeyModel> findAllByOwner(@Param("owner") UserModel owner);

    @Query("""
    SELECT COUNT(k) FROM ApiKeyModel k
    WHERE k.owner = :owner AND k.status.id = 1
""")
    int countByOwnerAndStatus(@Param("owner") UserModel owner);

    @Modifying
    @Query("""
     UPDATE ApiKeyModel k SET k.previousHashedSecret = null
     WHERE
     k.previousHashedSecret IS NOT NULL AND
     k.rotatedAt IS NOT NULL AND
     k.rotatedAt < :sixHoursAgo
""")
    void revokePreviousHash(Instant sixHoursAgo);
}
