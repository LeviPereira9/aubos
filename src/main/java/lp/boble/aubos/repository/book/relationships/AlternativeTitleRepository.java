package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.AlternativeTitleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AlternativeTitleRepository extends JpaRepository<AlternativeTitleModel, UUID> {


    List<AlternativeTitleModel> findAllByBook_Id(UUID bookId);

    boolean existsByBook_IdAndTitleIgnoreCase(UUID bookId, String title);

    @Query("""
    SELECT at.title FROM AlternativeTitleModel at WHERE at.book.id = :bookId AND at.title IN :titles
""")
    List<String> findExistingTitles(UUID bookId, Set<String> titles);
}
