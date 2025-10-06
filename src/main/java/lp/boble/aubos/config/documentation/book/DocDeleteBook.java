package lp.boble.aubos.config.documentation.book;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Excluir livro",
        description = "Remove temporariamente um livro do sistema. Esta ação pode ser desfeita."
)
public @interface DocDeleteBook {}
