package lp.boble.aubos.config.documentation.auth;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Alterar a role de um usuário",
        description = "Altera a role de um usuário que está abaixo da hierarquia do requester."
)
public @interface DocModifyUserRole {}
