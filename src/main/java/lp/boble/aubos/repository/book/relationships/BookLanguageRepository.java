package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookLanguageRepository extends JpaRepository<BookLanguage, UUID> {

    @Modifying
    @Query("""
    DELETE FROM BookLanguage b WHERE b.book = :book
""")
    void deleteByBook(BookModel book);

    List<BookLanguage> findAllByBookId(UUID bookId);

    boolean existsByBookIdAndLanguageId(UUID bookId, int id);

    List<Integer> findAllLanguageIdByBookId(UUID bookId);
}
