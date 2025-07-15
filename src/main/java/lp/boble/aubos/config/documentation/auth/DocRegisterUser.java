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
        summary = "Cria um novo usuário",
        description = "Cria um novo usuário e retorna o TokenModel para Login"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(
                responseCode = "409",
                description = "Username/Email já estão cadastrados",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Username/Email fornecidos são inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DocRegisterUser {}
