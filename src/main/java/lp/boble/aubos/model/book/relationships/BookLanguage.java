package lp.boble.aubos.model.book.relationships;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;

import java.util.UUID;

@Entity
@Table(name = "tb_book_languages")
@Data
public class BookLanguage {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageModel language;
}
