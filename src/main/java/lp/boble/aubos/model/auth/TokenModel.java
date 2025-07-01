package lp.boble.aubos.model.auth;


import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;

@Entity
@Table(name = "tb_tokens")
@Data
public class TokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserModel user;

    private String token;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    boolean used;
    boolean revoked;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private TokenTypeModel type;
}
