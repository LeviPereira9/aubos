package lp.boble.aubos.config.documentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lp.boble.aubos.config.docSnippets.UsernameErrors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar usuário por username",
        description = "Qualquer usuário pode fazer esta ação, retorna apenas informações não sensíveis de usuários",
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.")
})
@UsernameErrors
public @interface DocGetUserShortInfo {}
