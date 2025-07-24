package lp.boble.aubos.model.book.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.user.RoleModel;

import java.util.UUID;

@Entity
@Table(name = "tb_book_contributor")
@Data
public class BookContributor {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "contributor_id")
    @JsonIgnore
    private ContributorModel contributor;

    @ManyToOne
    @JoinColumn(name = "contributor_role_id")
    private RoleModel contributorRole;
}
