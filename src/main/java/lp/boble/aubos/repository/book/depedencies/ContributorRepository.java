package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.dto.contributor.ContributorPageProjection;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContributorRepository extends JpaRepository<ContributorModel, UUID> {

    @Query("""
    SELECT EXISTS (FROM ContributorModel c
     WHERE c.id = :id AND c.createdBy = :owner)
""")
    boolean isOwner(UUID id, UserModel owner);

    @Query("""
    SELECT
     c.id as id,
     c.name as name,
     c.lastUpdate as lastUpdate
     FROM ContributorModel c
     WHERE c.name LIKE CONCAT('%', :search, '%') AND c.softDeleted = FALSE
""")
    Page<ContributorPageProjection> getAllContributorsSuggestion(Pageable pageable,@Param("search") String search);


    @Query("""
    SELECT EXISTS (FROM ContributorModel c WHERE c.name = :name)
""")
    boolean nameIsAvailable(@Param("name") String name);

    @Query("""
    SELECT c.lastUpdate FROM ContributorModel c
    WHERE c.id = :id
    """)
    Optional<Instant> getLastUpdate(UUID id);

    @Query("""
    SELECT c FROM ContributorModel c
    WHERE c.id = :id AND c.softDeleted = false
""")
    Optional<ContributorModel> findContributorById(@Param("id") UUID id);

    @Query("""
    SELECT c.id FROM ContributorModel c WHERE c.id IN :ids
""")
    List<UUID> findAllByIdIn(@Param("ids") List<UUID> ids);
}
