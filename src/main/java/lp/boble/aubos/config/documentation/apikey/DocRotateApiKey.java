package lp.boble.aubos.config.documentation.apikey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;
import lp.boble.aubos.response.error.ErrorResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Rotação de Chave",
        description = "Gera uma nova secret, mantendo ainda a mesma chave"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "Chave rotacionada com sucesso..."),
        @ApiResponse(
                responseCode = "404",
                description = "Chave não encontrada.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
@UsernameErrors
@SelfOrModError
public @interface DocRotateApiKey {}
