package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.TypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TypeRepository extends JpaRepository<TypeModel, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT t.name FROM TypeModel t WHERE t.name IN :requestTypes
""")
    List<String> findByName(Set<String> requestTypes);
}
