package lp.boble.aubos.model.book.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lp.boble.aubos.model.book.BookModel;

import java.util.UUID;

@Entity
@Table(name = "tb_book_alternative_title")
public class BookAlternativeTitleModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @Column(name = "alternative_title")
    private String alternativeTitle;

}
