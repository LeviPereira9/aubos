package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookContributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookContributorRepository extends JpaRepository<BookContributor, UUID> {}
