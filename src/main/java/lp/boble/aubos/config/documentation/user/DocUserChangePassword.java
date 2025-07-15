package lp.boble.aubos.config.documentation.user;

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
        summary = "Mudança de senha",
        description = "Mudança de senha"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso."),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Requester não é o target",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Nova senha igual antiga, senha nova e confirmação não conferem",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
@SelfOrModError
@UsernameErrors
public @interface DocUserChangePassword {}
