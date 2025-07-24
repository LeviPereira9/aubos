package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_book_type")
@Data
public class TypeModel {
    @Id
    private int id;

    @Column(name = "name")
    private String name;
}
