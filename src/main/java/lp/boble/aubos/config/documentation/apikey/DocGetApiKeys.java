package lp.boble.aubos.config.documentation.apikey;

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
        summary = "Listar todas as chaves ativas",
        description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chaves encontradas com sucesso/Usuário não possui nenhuma chave")
})
@UsernameErrors
@SelfOrModError
public @interface DocGetApiKeys {}
