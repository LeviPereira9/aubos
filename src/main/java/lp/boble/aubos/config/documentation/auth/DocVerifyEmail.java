package lp.boble.aubos.config.documentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.response.error.ErrorResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Verificação de email",
        description = "Verifica o e-mail do usuário"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail verificado com sucesso."),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário/Token não encontrado.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public @interface DocVerifyEmail {}
