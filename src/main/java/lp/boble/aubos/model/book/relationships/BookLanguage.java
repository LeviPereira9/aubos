package lp.boble.aubos.model.book.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.LanguageModel;

import java.util.Objects;
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
    @JsonIgnore
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageModel language;

    public BookLanguage() {}
    public BookLanguage(BookModel book, LanguageModel language) {
        this.book = book;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLanguage that = (BookLanguage) o;
        return Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language);
    }
}
