package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookLanguageRepository extends JpaRepository<BookLanguage, Integer> {

    @Modifying
    @Query("""
    DELETE FROM BookLanguage b WHERE b.book = :book
""")
    void deleteByBook(BookModel book);
}
