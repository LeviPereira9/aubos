package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_book_type")
@Data
public class TypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;
}
