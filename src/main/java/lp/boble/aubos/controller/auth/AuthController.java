package lp.boble.aubos.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;
import lp.boble.aubos.dto.auth.*;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth",
        description = "Endpoints para gerencimaneto de autenticação de usuários")
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    registerUser(@RequestBody AuthRegisterRequest registerRequest) {

        AuthResponse responseData = authService.register(registerRequest);

        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Usuário registrado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Entrar na conta",
            description = "Retorna um TokenModel para acesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Login/Senha fornecido é inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    login(@RequestBody AuthLoginRequest loginRequest) {

        AuthResponse responseData = authService.login(loginRequest);


        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Usuário logado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponse<Void>> forgotPassword(@RequestBody AuthForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Código enviado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    };

    @Operation(
            summary = "Validador de Token",
            description = "Valida tokens gerados para operação de reset de senha e verificação de e-mail"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válidado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/validate-token")
    public ResponseEntity<SuccessResponse<Void>> validateRequestToken(
            @RequestBody AuthTokenRequest requestToken){

        authService.validateToken(requestToken.token(), requestToken.type());

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Token válidado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(
            summary = "Alteração de senha esquecida",
            description = "Alterar a senha do usuário por meio do Token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário/Token não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Senha antiga igual a nova, nova senha e confirmação não confere",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/forgot-password")
    public ResponseEntity<SuccessResponse<Void>> changePassword(
            @RequestParam String token,
            @RequestBody AuthChangePasswordRequest request
    ){

        authService.changePassword(token, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Senha alterada com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Verificação de email",
            description = "Verifica o e-mail do usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail verificado com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário/Token não encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/verify-email")
    public ResponseEntity<SuccessResponse<Void>> verifyEmail(
            @RequestParam String token
    ){

        authService.confirmEmailVerification(token);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("E-mail verificado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Encerrador de sessão global", description = "Encerra todas as sessões abertas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todas as sessões abertas foram encerradas."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @UsernameErrors
    @SelfOrModError
    @PostMapping("/{username}/globalLogout")
    public ResponseEntity<SuccessResponse<Void>> globalLogout(
            @PathVariable String username
    ){
        authService.globalLogout(username);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Todas as sessões abertas foram encerradas.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
