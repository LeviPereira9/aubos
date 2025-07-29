package lp.boble.aubos.repository.book;

import lp.boble.aubos.dto.book.BookPageProjection;
import lp.boble.aubos.model.book.BookModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {

    Optional<BookModel> findByIdAndSoftDeletedFalse(UUID id);

    @Query("""
    SELECT b.lastUpdated FROM BookModel b
        WHERE b.id = :id AND b.softDeleted = false
""")
    Optional<Instant> getLastUpdated(UUID id);

    @Query("""
    SELECT
         b.id as id,
         b.title as title,
         b.subtitle as subtitle,
         b.synopsis as synopsis,
         b.coverUrl as coverUrl,
         b.status.label as status,
         b.lastUpdated as lastUpdated
     FROM BookModel b WHERE b.title LIKE CONCAT('%', :search, '%') OR b.subtitle LIKE CONCAT('%', :search, '%')
""")
    Page<BookPageProjection> getAllShortBookInfo(Pageable pageable, @Param("search") String search);


    /*@Query("""
    SELECT
         MAX(b.lastUpdated)
     FROM BookModel b WHERE b.title LIKE CONCAT('%', :search, '%') OR b.subtitle LIKE CONCAT('%', :search, '%')
""")
    Optional<Instant> getShortBookLastUpdated(Pageable pageable,String search);*/
}
