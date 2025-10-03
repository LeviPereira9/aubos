package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.LicenseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<LicenseModel, Integer> {
    boolean existsByLabel(String label);
}
