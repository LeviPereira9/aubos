package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_book_restriction")
@Data
public class RestrictionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "age")
    private int age;

    @Column(name = "description")
    private String description;

}
