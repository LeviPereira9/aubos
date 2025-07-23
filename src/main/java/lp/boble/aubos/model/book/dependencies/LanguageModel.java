package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_language")
@Data
public class LanguageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
