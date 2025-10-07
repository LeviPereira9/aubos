package lp.boble.aubos.model.apikey;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lp.boble.aubos.model.user.UserModel;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_api_keys")
@Data
public class ApiKeyModel {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;

    @Column(name = "public_id", nullable = false, length = 128)
    private String publicId;

    @Column(name = "hashed_secret", nullable = false, length = 128)
    private String hashedSecret;

    @Column(name = "previous_hashed_secret", length = 64)
    private String previousHashedSecret;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel owner;

    @Size(max = 100)
    @Column(name = "label", length = 100)
    private String label;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @ColumnDefault("1000")
    @Column(name = "rate_limit")
    private Integer rateLimit = 1000;

    @ColumnDefault("0")
    @Column(name = "request_count")
    private Integer requestCount = 0;

    @Column(name = "reset_at")
    private Instant resetAt;

    @Column(name = "rotated_at")
    private Instant rotatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnDefault("'1'")
    @JoinColumn(name = "status")
    private ApiKeyStatusesModel status;

    @ColumnDefault("0")
    @Column(name = "soft_delete")
    private Boolean softDelete;

    public void incrementRequestCount() {
        this.requestCount++;
    }

    public boolean isExpired(){
        return this.expiresAt.isBefore(Instant.now());
    }

    @PrePersist
    public void prePersist(){
        if(this.status == null){
            ApiKeyStatusesModel defaultStatus = new ApiKeyStatusesModel();
            defaultStatus.setId((byte) 1);
            this.status = defaultStatus;
        }
        if(this.softDelete == null){
            this.softDelete = false;
        }
    }

}
