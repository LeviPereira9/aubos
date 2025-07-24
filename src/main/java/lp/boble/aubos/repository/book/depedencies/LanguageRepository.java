package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<LanguageModel, Integer> {}
