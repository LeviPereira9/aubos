package lp.boble.aubos.model.book.relationships;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_book_family_membership")
@Data
public class BookFamilyModel {
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

    public UUID getBookId(){
        return book.getId();
    }

    public void markAsTempOrder(){
        this.orderInFamily *= -1;
    }

    public void restoreFinalOrder(){
        this.orderInFamily *= -1;
    }

    public void adjustOrderToAvoidConflict(List<Integer> ordersInUse){
        int orderToAdd = this.getOrderInFamily();
        boolean orderConflict = ordersInUse.contains(orderToAdd);

        if(orderConflict){
            orderToAdd = Collections.max(ordersInUse) + 1;
            this.setOrderInFamily(orderToAdd);
        }

        ordersInUse.add(orderToAdd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookFamilyModel that = (BookFamilyModel) o;
        return Objects.equals(orderInFamily, that.orderInFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderInFamily);
    }
}
