package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BookLanguageRepository extends JpaRepository<BookLanguage, UUID> {

    @Modifying
    @Query("""
    DELETE FROM BookLanguage b WHERE b.book = :book
""")
    void deleteByBook(BookModel book);

    List<BookLanguage> findAllByBook_Id(UUID bookId);

    boolean existsByBook_IdAndLanguage_Id(UUID bookId, int id);

    @Query("""
    SELECT bl.language.id FROM BookLanguage bl WHERE bl.book.id = :bookId
""")
    List<Integer> findLanguageIdsByBookId(UUID bookId);

    List<BookLanguage> findAllByBook_IdAndIdIn(UUID bookId, List<UUID> bookLanguageIds);
}
