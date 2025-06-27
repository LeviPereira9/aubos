package lp.boble.aubos.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Data
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 100)
    @NotNull
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 255)
    @Column(name = "profile_pic")
    private String profilePic;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "join_date")
    private Instant joinDate = Instant.now();

    @Column(name = "last_login")
    private Instant lastLogin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private AccountStatusModel status;

    @ColumnDefault("0")
    @Column(name = "is_verified")
    private Boolean isVerified;

    @ColumnDefault("0")
    @Column(name = "is_official")
    private Boolean isOfficial;

    @Column(name = "soft_deleted")
    private Boolean softDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role")
    private RoleModel role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = this.role.getName();

        if(role.equals("admin")) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_MOD"),
                    new SimpleGrantedAuthority("ROLE_READER")
            );
        } else if(role.equals("mod")) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_MOD"),
                    new SimpleGrantedAuthority("ROLE_READER")
            );
        }else{
            return List.of(
                    new SimpleGrantedAuthority("ROLE_READER")
            );
        }

    }

    @Override
    public String getPassword() {
        return getPasswordHash();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // O hibernate automaticamente seta todos os valores não preenchidos como NULL
    // Por isso temos que iniciar o valor default aqui, utilizando o
    // PrePersist, que é utilizado somente antes de salvar a entidade.
    @PrePersist
    public void prePersist(){
        if(this.status == null){
            AccountStatusModel defaultStatus = new AccountStatusModel();
            defaultStatus.setId((short)1);
            this.status = defaultStatus;
        }
        if(this.role == null){
            RoleModel defaultRole = new RoleModel();
            defaultRole.setId((short)1);
            this.role = defaultRole;
        }
        if(this.isOfficial == null){
            this.isOfficial = false;
        }
        if(this.isVerified == null){
            this.isVerified = false;
        }
    }
}
