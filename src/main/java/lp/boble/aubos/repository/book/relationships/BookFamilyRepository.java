package lp.boble.aubos.repository.book.relationships;

import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookFamilyRepository extends JpaRepository<BookFamilyModel, UUID> {
    List<BookFamilyModel> findAllByFamily_Id(UUID familyId);

    void deleteByFamily_IdAndBook_Id(UUID family, UUID book);

    boolean existsByFamily_IdAndBook_Id(UUID family, UUID book);

    boolean existsByFamily_IdAndOrderInFamily(UUID family, int order);

    Optional<BookFamilyModel> findByFamily_IdAndBook_Id(UUID family, UUID book);

    @Query("""
    SELECT COALESCE(MAX(bf.orderInFamily), 0) FROM BookFamilyModel bf WHERE bf.family.id = :familyId
 """)
    Integer findMaxOrderInFamilyByFamilyId(@Param("familyId") UUID familyId);




    List<BookFamilyModel> findAllByFamily_IdAndIdIn(UUID familyId, List<UUID> membersId);
}
