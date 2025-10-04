package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.RestrictionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictionRepository extends JpaRepository<RestrictionModel, Integer> {

    boolean existsByAge(int requestAge);
}
