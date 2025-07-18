package lp.boble.aubos.config.documentation.apikey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Revogar chave anterior",
        description = "Revoga a chave anterior a rotação")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chave revogada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Chave não encontrada")
})
@UsernameErrors
@SelfOrModError
public @interface DocRevokePreviousHash {}
