package lp.boble.aubos.repository.book;

import lp.boble.aubos.model.book.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {

    Optional<BookModel> findByIdAndSoftDeletedTrue(UUID id);

    @Query("""
    SELECT b.lastUpdated FROM BookModel b
        WHERE b.id = :id AND b.softDeleted = false
""")
    Optional<Instant> getLastUpdated(UUID id);
}
