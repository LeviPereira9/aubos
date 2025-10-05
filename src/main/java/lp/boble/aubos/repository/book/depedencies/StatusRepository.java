package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.StatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface StatusRepository extends JpaRepository<StatusModel, Integer> {
    boolean existsByLabel(String requestedLabel);

    @Query("""
    SELECT s.label FROM StatusModel s WHERE s.label IN :requestedLabels
""")
    List<String> findAllLabels(Set<String> requestedLabels);
}
