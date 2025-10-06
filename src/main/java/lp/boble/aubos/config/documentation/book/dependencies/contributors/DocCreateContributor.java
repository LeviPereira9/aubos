package lp.boble.aubos.config.documentation.book.dependencies.contributors;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Criar novo contribuidor",
        description = "Cadastra um novo contribuidor no sistema."
)
public @interface DocCreateContributor {}
