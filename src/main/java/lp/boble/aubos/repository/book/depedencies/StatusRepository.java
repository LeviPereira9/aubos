package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.StatusModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<StatusModel, Integer> {
    boolean existsByLabel(String requestedLabel);
}
