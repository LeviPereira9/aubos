package lp.boble.aubos.config.documentation.book;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar sugestões de livros",
        description = "Retorna sugestões de livros paginadas baseadas na consulta de busca. Útil para autocomplete e funcionalidades de busca."
)
public @interface DocGetBookSuggestions {}
