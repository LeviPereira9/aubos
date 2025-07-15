package lp.boble.aubos.config.documentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Código de verificação do e-mail",
        description = "Envio do código de verificação do e-mail"
)
@ApiResponse(responseCode = "200", description = "Código enviado com sucesso.")
@SelfOrModError
@UsernameErrors
public @interface DocSendConfirmationEmail {}
