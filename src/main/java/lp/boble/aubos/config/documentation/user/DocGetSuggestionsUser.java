package lp.boble.aubos.config.documentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lp.boble.aubos.response.error.ErrorResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Listar usuários pelo termo de busca",
        description = "Qualquer usuário pode fazer esta ação, retorna usuários que compartilhem do termo de busca semelhantes ao username ou displayname",
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Termo inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
})
public @interface DocGetSuggestionsUser {}
