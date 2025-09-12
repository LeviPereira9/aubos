package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.ContributorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ContributorRoleRepository extends JpaRepository<ContributorRole, Integer> {

    @Query("""
    SELECT r.id FROM ContributorRole r WHERE r.id IN :ids
""")
    List<Integer> findAllByIdIn(@Param("ids") List<Integer> ids);
}
