package lp.boble.aubos.model.book.family;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_book_family_type")
@Data
public class FamilyType {

    @Id
    private Long id;
    private String value;
}
