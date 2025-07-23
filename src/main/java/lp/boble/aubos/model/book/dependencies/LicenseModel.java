package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_book_license")
@Data
public class LicenseModel {
    @Id
    private int id;

    @Column(name = "label")
    private String label;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "description")
    private String description;
}
