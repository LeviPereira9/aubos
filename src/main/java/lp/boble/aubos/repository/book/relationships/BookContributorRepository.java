package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BookContributorRepository extends JpaRepository<BookContributor, UUID> {
    @Modifying
    @Query("""
    DELETE BookContributor b WHERE b.book = :book
""")
    void deleteByBook(BookModel book);
}
