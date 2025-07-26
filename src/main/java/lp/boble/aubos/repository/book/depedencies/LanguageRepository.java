package lp.boble.aubos.repository.book.depedencies;

import lp.boble.aubos.model.book.dependencies.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<LanguageModel, Integer> {

    @Query("""
    SELECT l FROM LanguageModel l WHERE l.id IN :ids
""")
    Optional<List<LanguageModel>> findAllAvailableLanguages(List<Integer> ids);

}
