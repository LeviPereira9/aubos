package lp.boble.aubos.config.documentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar um usuário pelo username",
        description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
})
@SelfOrModError
@UsernameErrors
public @interface DocGetUserInfo {}
