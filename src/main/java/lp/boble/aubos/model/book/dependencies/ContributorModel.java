package lp.boble.aubos.model.book.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_contributor")
@Data
public class ContributorModel {
    @Id
    @GeneratedValue
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

    @OneToMany(mappedBy = "contributor")
    @JsonIgnore
    private List<BookContributor> books;
}
