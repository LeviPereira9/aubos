package lp.boble.aubos.model.book.relationships;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_book_metrics")
@Data
public class Metrics {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @Column(name = "qty_chapters")
    private Long qtyChapters;
    @Column(name = "qty_views")
    private Long qtyViews;
    @Column(name = "qty_favorites")
    private Long qtyFavorites;

    @Column(name = "last_update")
    private Instant lastUpdate;
}
