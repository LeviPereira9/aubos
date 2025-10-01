package lp.boble.aubos.model.book.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.TagModel;

import java.util.UUID;

@Entity
@Table(name = "tb_book_tag")
public class BookTagModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private TagModel tag;


}
