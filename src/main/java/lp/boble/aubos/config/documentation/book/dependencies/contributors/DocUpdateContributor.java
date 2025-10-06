package lp.boble.aubos.config.documentation.book.dependencies.contributors;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Atualizar contribuidor",
        description = "Atualiza as informações de um contribuidor existente. Retorna o contribuidor atualizado."
)
public @interface DocUpdateContributor {}
