package lp.boble.aubos.config.documentation.book;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Criar um novo livro",
        description = "Cria um novo livro com os detalhes fornecidos. Retorna o livro criado com o ID gerado."
)
public @interface DocPostBook {}
