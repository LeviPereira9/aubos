package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_book_restriction")
@Data
public class Restriction {

    @Id
    private int id;

    @Column(name = "age")
    private int age;

    @Column(name = "description")
    private String description;

}
