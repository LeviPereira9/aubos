package lp.boble.aubos.config.documentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(summary = "Encerrador de sessão global", description = "Encerra todas as sessões abertas.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todas as sessões abertas foram encerradas."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
})
@UsernameErrors
@SelfOrModError
public @interface DocGlobalLogout {}
