package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_contributor_roles")
@Data
public class ContributorRole {
    @Id
    private int id;
    private String role;
}
