package lp.boble.aubos.model.book.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.relationships.BookLanguage;

import java.util.List;

@Entity
@Table(name = "tb_language")
@Data
public class LanguageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "value")
    private String value;

    @OneToMany(mappedBy = "language")
    @JsonIgnore
    private List<BookLanguage> bookLanguage;
}
