package lp.boble.aubos.config.documentation.book;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Atualizar livro",
        description = "Atualiza as informações de um livro existente. Retorna o livro atualizado."
)
public @interface DocUpdateBook {}
