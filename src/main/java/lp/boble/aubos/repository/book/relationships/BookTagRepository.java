package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookTagModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookTagRepository extends JpaRepository<BookTagModel, UUID> {

    List<BookTagModel> findAllByBook_id(UUID bookId);

    boolean existsByBookIdAndTagId(UUID bookId, int id);
}
