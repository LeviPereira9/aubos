package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookContributorRepository extends JpaRepository<BookContributorModel, UUID> {

    List<BookContributorModel> findAllByBookIdAndContributorRoleId(UUID bookId, int contributorRoleId);

    List<BookContributorModel> findAllByBookId(UUID bookId);

    boolean existsByBookIdAndContributorIdAndContributorRoleId(UUID bookId,UUID contributorId, int contributorRoleId);

    @Query("""
    SELECT bc FROM BookContributorModel bc WHERE bc.book.id = :bookId AND bc.id IN :ids
""")
    List<BookContributorModel> findAllByIdIn(@Param("id") UUID bookId, @Param("ids") List<UUID> ids);

    List<BookContributorModel> findAllByBookIdAndIdIn(UUID bookId, List<UUID> ids);
}
