package lp.boble.aubos.model.book.relationships;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.FamilyModel;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_book_family_membership")
@Data
public class BookFamilyMembership {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "book_family_id")
    private FamilyModel family;

    @Column(name = "order_in_family")
    private int orderInFamily;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserModel createdBy;

    @Column(name = "last_update")
    private Instant lastUpdate;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private UserModel updatedBy;
}
