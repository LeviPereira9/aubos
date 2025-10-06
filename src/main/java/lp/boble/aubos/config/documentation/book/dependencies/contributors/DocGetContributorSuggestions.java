package lp.boble.aubos.config.documentation.book.dependencies.contributors;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar sugestões de contribuidores",
        description = "Retorna sugestões de contribuidores paginadas baseadas na consulta de busca."
)
public @interface DocGetContributorSuggestions {}
