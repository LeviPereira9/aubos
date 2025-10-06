package lp.boble.aubos.config.documentation.book.dependencies.contributors;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar contribuidor por ID",
        description = "Recupera um contribuidor específico pelo seu identificador único. Suporta ETag para cache."
)
public @interface DocGetContributor {}
