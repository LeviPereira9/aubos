package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookContributorRepository extends JpaRepository<BookContributorModel, UUID> {

    List<BookContributorModel> findAllByBook_IdAndContributorRole_Id(UUID bookId, int contributorRoleId);

    List<BookContributorModel> findAllByBook_Id(UUID bookId);

    boolean existsByBook_IdAndContributor_IdAndContributorRole_Id(UUID bookId, UUID contributorId, int contributorRoleId);


    List<BookContributorModel> findAllByBook_IdAndIdIn(UUID bookId, List<UUID> ids);
}
