package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookFamilyRepository extends JpaRepository<BookFamilyModel, UUID> {
    List<BookFamilyModel> findAllByFamilyId(UUID familyId);

    void deleteByFamilyIdAndBookId(UUID family, UUID book);

    boolean existsByFamilyIdAndBookId(UUID family, UUID book);

    @Query("""
    SELECT EXISTS (SELECT bf FROM BookFamilyModel bf WHERE bf.family.id = :family AND bf.book.id = :book)
""")
    boolean check(UUID family, UUID book, int order);

    boolean existsByFamilyIdAndOrderInFamily(UUID family, int order);

    Optional<BookFamilyModel> findByFamilyIdAndBookId(UUID family, UUID book);

    int findMaxOrderInFamilyByFamilyId(UUID familyId);

    List<Integer> findAllOrderInFamilyByFamilyId(UUID familyId);

    @Query("""
     SELECT bf.book.id FROM BookFamilyModel bf WHERE bf.id = :familyId
""")
    List<UUID> findAllBookIdsByFamilyId(UUID familyId);
}
