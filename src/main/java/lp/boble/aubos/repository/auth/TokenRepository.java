package lp.boble.aubos.repository.auth;

import lp.boble.aubos.model.auth.TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenModel, Long> {

    @Query(value = """
        SELECT count(t) > 0 FROM TokenModel t
        WHERE
            t.token = :token AND
            t.type.id = :type AND
            (t.used = TRUE OR t.revoked = TRUE)
""")
    boolean alreadyUsed(String token, Long type);

    @Query(value = """
        SELECT count(t) > 0 FROM TokenModel t
        WHERE
            t.token = :token AND
            t.type = :type AND
            t.used = false AND
            t.revoked = false
""")
    boolean isPending(String token, Long type);

    @Query(value = """
        SELECT t FROM TokenModel t
        WHERE
            t.token = :token AND
            t.type.id = :type AND
            t.used = false AND
            t.revoked = false
""")
    Optional<TokenModel> findByToken(String token, Long type);

    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE TokenModel t
        SET
            t.revoked = true,
            t.updatedAt = :now
        WHERE
            t.expiresAt < :now AND
            t.used = false AND
            t.revoked = false
       """)
    void disableToken(Instant now);
}
