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
        summary = "Recuperação de senha",
        description = "Envio do código de recuperação de senha"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código enviado com sucesso."),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "503",
                description = "Envio de e-mail fora do ar",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DocForgotPassword {}
