package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_book_status")
@Data
public class StatusModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "label")
    private String label;
}
