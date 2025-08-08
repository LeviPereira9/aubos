package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookFamilyRepository extends JpaRepository<BookFamilyModel, UUID> {
    List<BookFamilyModel> findAllByFamilyId(UUID familyId);

    void deleteByFamilyIdAndBookId(UUID family, UUID book);

    boolean existsByFamilyIdAndBookId(UUID family, UUID book);
}
