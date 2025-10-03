package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.LicenseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LicenseRepository extends JpaRepository<LicenseModel, Integer> {
    boolean existsByLabel(String label);

    @Query("""
    SELECT l.label FROM LicenseModel l WHERE l.label IN :requestedLabels
""")
    List<String> findExistingLabels(Set<String> requestedLabels);
}
