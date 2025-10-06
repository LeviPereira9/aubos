package lp.boble.aubos.config.documentation.book.dependencies.contributors;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Excluir contribuidor",
        description = "Marca um contribuidor como excluído no sistema. Essa ação pode ser desfeita."
)
public @interface DocDeleteContributor {}
