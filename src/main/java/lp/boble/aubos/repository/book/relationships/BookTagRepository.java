package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookTagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookTagRepository extends JpaRepository<BookTagModel, UUID> {

    List<BookTagModel> findAllByBook_id(UUID bookId);

    boolean existsByBookIdAndTagId(UUID bookId, int id);

    @Query("""
    SELECT bt.tag.id FROM BookTagModel bt WHERE bt.book.id = :bookId AND bt.tag.id IN :requestedTagIds
""")
    List<Integer> findExistingTagsId(UUID bookId, Set<Integer> requestedTagIds);

    @Query("""
    SELECT bt FROM BookTagModel bt WHERE bt.book.id = :bookId AND bt.id IN :bookTagIds
""")
    List<BookTagModel> findCurrentTagsInBook(UUID bookId, Set<UUID> bookTagIds);
}
