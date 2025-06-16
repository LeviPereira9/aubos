package lp.boble.aubos.config.docSnippets;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lp.boble.aubos.response.error.ErrorResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Esta ação só pode ser realizada pelo próprio usuário ou um MOD",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface SelfOrModError {}
