package lp.boble.aubos.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.user.UserAuthResponse;
import lp.boble.aubos.dto.user.UserLoginRequest;
import lp.boble.aubos.dto.user.UserRegisterRequest;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.auth.AuthService;
import lp.boble.aubos.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Auth",
        description = "Endpoints para gerencimaneto de autenticação de usuários")
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
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
    public ResponseEntity<SuccessResponse<UserAuthResponse>>
    registerUser(@RequestBody UserRegisterRequest registerRequest) {

        UserAuthResponse responseData = authService.register(registerRequest);

        SuccessResponse<UserAuthResponse> response =
                new SuccessResponseBuilder<UserAuthResponse>()
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
    public ResponseEntity<SuccessResponse<UserAuthResponse>>
    login(@RequestBody UserLoginRequest loginRequest) {

        UserAuthResponse responseData = authService.login(loginRequest);


        SuccessResponse<UserAuthResponse> response =
                new SuccessResponseBuilder<UserAuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Usuário logado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
