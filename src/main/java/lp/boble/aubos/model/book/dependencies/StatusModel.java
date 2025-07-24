package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_book_status")
@Data
public class StatusModel {
    @Id
    private int id;

    @Column(name = "label")
    private String label;
}
