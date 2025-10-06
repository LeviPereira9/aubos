package lp.boble.aubos.config.documentation.book;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar livro por ID",
        description = "Retorna um livro específico pelo seu ID. Suporta ETag para cache."
)
public @interface DocGetBook {}
