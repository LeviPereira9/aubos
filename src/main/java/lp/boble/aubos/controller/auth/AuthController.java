package lp.boble.aubos.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.documentation.auth.*;
import lp.boble.aubos.dto.auth.*;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Auth",
        description = "Endpoints para gerencimaneto de autenticação de usuários")
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @DocRegisterUser
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    registerUser(@RequestBody AuthRegisterRequest registerRequest) {

        AuthResponse content = authService.register(registerRequest);

        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Usuário registrado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DocLogin
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    login(@RequestBody AuthLoginRequest loginRequest) {

        AuthResponse content = authService.login(loginRequest);


        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Usuário logado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DocForgotPassword
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

    @DocValidateRequestToken
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


    @DocAuthChangePassword
    @PatchMapping("/forgot-password")
    public ResponseEntity<SuccessResponse<Void>> changePassword(
            @RequestParam String token,
            @RequestBody AuthChangePasswordRequest request
    ){

        authService.changeUserPassword(token, request);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Senha alterada com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DocVerifyEmail
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

    @DocGlobalLogout
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

    @PutMapping("/{userId}/{roleName}")
    public ResponseEntity<SuccessResponse<Void>> modifyUserRole(
            @PathVariable UUID userId,
            @PathVariable String roleName
            ){

        authService.updateUserRole(userId, roleName);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("PUT")
                        .message("Role atualizada com sucesso.")
                        .code(HttpStatus.OK)
                        .build();

        return ResponseEntity.ok(response);
    }

}
