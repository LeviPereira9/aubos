package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface RestrictionRepository extends JpaRepository<RestrictionModel, Integer> {

    boolean existsByAge(int requestAge);

    @Query("""
    SELECT r.age FROM RestrictionModel r WHERE r.age IN :requestedAges
""")
    List<Integer> findAllByAge(Set<Integer> requestedAges);
}
