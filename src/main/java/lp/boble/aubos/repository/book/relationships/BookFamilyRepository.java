package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookFamilyRepository extends JpaRepository<BookFamilyModel, UUID> {
    List<BookFamilyModel> findAllByFamilyId(UUID familyId);

    void deleteByFamilyIdAndBookId(UUID family, UUID book);

    boolean existsByFamilyIdAndBookId(UUID family, UUID book);

    boolean existsByFamilyIdAndOrderInFamily(UUID family, int order);

    Optional<BookFamilyModel> findByFamilyIdAndBookId(UUID family, UUID book);

    @Query("""
    SELECT COALESCE(MAX(bf.orderInFamily), 0) FROM BookFamilyModel bf WHERE bf.family.id = :familyId
 """)
    Integer findMaxOrderInFamilyByFamilyId(@Param("familyId") UUID familyId);


    @Query("""
    SELECT bf.orderInFamily FROM BookFamilyModel bf WHERE bf.family.id = :familyId
""")
    List<Integer> findAllOrderInFamilyByFamilyId(@Param("familyId") UUID familyId);

    @Query("""
     SELECT bf.book.id FROM BookFamilyModel bf WHERE bf.family.id = :familyId
""")
    List<UUID> findAllBookIdsByFamilyId(@Param("familyId") UUID familyId);
}
