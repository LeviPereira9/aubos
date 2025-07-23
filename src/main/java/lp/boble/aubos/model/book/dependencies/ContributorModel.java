package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_contributor")
@Data
public class ContributorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserModel createdBy;

    @Column(name = "last_update")
    private Instant lastUpdate;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private UserModel updatedBy;
}
