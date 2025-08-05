package lp.boble.aubos.repository.book.family;

import lp.boble.aubos.model.book.family.FamilyModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyRepository extends JpaRepository<FamilyModel, UUID> {}
