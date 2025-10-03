package lp.boble.aubos.model.book.relationships;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;

import java.util.UUID;

@Entity
@Table(name = "tb_book_alternative_title")
@Data
public class AlternativeTitleModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @Column(name = "alternative_title")
    private String title;

    public boolean belongsTo(UUID bookId) {
        return this.book.getId().equals(bookId);
    }
}
