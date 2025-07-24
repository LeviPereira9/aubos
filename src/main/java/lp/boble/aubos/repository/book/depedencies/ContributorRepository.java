package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.ContributorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContributorRepository extends JpaRepository<ContributorModel, UUID> {}
