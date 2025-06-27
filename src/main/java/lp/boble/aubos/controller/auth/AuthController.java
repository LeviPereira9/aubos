package lp.boble.aubos.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.*;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
            description = "Cria um novo usuário e retorna o Token para Login"
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
            description = "Retorna um Token para acesso"
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

    @PostMapping("/validate-token")
    public ResponseEntity<SuccessResponse<Void>> validateRequestToken(
            @RequestBody AuthResetTokenRequest requestToken){

        authService.validateResetToken(requestToken.token());

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Token válidado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<SuccessResponse<Void>> changePassword(
            @RequestParam String token,
            @RequestBody AuthChangePasswordRequest request
    ){

        authService.changePassword(token, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Senha alterada com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // TODO: Agendar Ripação: @Scheduled

}
