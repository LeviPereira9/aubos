package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_book_license")
@Data
public class LicenseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //TODO: id de vdd. :P
    private int id;

    @Column(name = "label")
    private String label;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "description")
    private String description;

    public boolean hasSameLabel(String label) {
        return this.label.equals(label);
    }
}
