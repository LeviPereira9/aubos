package lp.boble.aubos.config.documentation.apikey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.response.error.ErrorResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Desativar chave",
        description = "Apenas o próprio usuário ou um MOD podem realizar esta ação"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chave desativada com sucesso"),
        @ApiResponse(
                responseCode = "400",
                description = "Username/Public ID inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Username/Public ID não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
@SelfOrModError
public @interface DocDeleteApiKey {}
