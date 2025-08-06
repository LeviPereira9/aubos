package lp.boble.aubos.model.book.family;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_visibility")
@Data
public class Visibility {

    @Id
    private int id;

    @Column(name = "value")
    private String value;
}
