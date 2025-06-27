package lp.boble.aubos.repository.auth;

import lp.boble.aubos.model.auth.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long>{

    @Query(value = """
    SELECT COUNT(r) > 0 FROM ResetToken r WHERE r.token = :token AND r.used = true
""")
    boolean isTokenAlreadyUsed(@Param("token") String token);

    boolean existsByTokenAndUsedTrue(String token);

    boolean existsByTokenAndUsedFalse(String token);

    ResetToken findByToken(String token);

    @Modifying(clearAutomatically = true)
    @Query(value = """
    UPDATE ResetToken r SET r.used = true WHERE r.expiresAt < :now AND r.used = false
""")
    void disableToken(Instant now);
}
