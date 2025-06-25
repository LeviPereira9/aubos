package lp.boble.aubos.model.auth;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;

@Entity
@Table(name = "tb_reset_password_token")
@Data
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    private String token;

    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "expires_at")
    private Instant expiresAt;
    private boolean used;

}
