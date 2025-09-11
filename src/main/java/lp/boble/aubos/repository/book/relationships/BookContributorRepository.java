package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookContributorRepository extends JpaRepository<BookContributorModel, UUID> {

    List<BookContributorModel> findAllByBookIdAndContributorRoleId(UUID bookId, int contributorRoleId);

    List<BookContributorModel> findAllByBookId(UUID bookId);
}
