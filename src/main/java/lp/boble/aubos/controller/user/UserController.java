package lp.boble.aubos.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;
import lp.boble.aubos.dto.auth.AuthChangePasswordRequest;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User",
        description = "Endpoint de gerenciamento do usuário, incluindo registro, login, atualização, exclusão e busca de usuários."
)
@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Buscar um usuário pelo username",
            description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
    })
    @SelfOrModError
    @UsernameErrors
    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponse<UserResponse>>
    getUserInfo(@PathVariable String username){
        UserResponse responseData = userService.getUserInfo(username);

        SuccessResponse<UserResponse> response =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(
            summary = "Buscar usuário por username",
            description = "Qualquer usuário pode fazer esta ação, retorna apenas informações não sensíveis de usuários",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.")
    })
    @UsernameErrors
    @GetMapping("/{username}/details")
    public ResponseEntity<SuccessResponse<UserShortResponse>>
    getUserShortInfo(@PathVariable String username){
        UserShortResponse responseData = userService.getUserShortInfo(username);

        SuccessResponse<UserShortResponse> response =
                new SuccessResponseBuilder<UserShortResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Listar usernames e displaynames pelo termo de busca",
            description = "Qualquer usuário pode fazer esta ação, retorna usernames e seus status caso compartilhem do termo de busca semelhantes ao username ou displayname",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usernames encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Termo inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserAutocompleteProjection>>
    getAutocompleteUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<UserAutocompleteProjection> response =
                userService.getUserAutocomplete(query, page);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Listar usuários pelo termo de busca",
            description = "Qualquer usuário pode fazer esta ação, retorna usuários que compartilhem do termo de busca semelhantes ao username ou displayname",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Termo inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/suggestions")
    public ResponseEntity<PageResponse<UserSuggestionProjection>>
    getSuggestionsUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<UserSuggestionProjection> response =
                userService.getUserSuggestion(query, page);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Atualizar usuário",
            description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @SelfOrModError
    @UsernameErrors
    @PutMapping("/{username}")
    public ResponseEntity<SuccessResponse<UserResponse>>
    updateUser(@PathVariable String username,
               @RequestBody UserUpdateRequest updateRequest){

        UserResponse responseData = userService.updateUser(username, updateRequest);

        SuccessResponse<UserResponse> response =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Usuário atualizado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(
            summary = "Soft delete",
            description = "Apenas o próprio usuário ou um MOD podem realizar esta ação",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @SelfOrModError
    @UsernameErrors
    @DeleteMapping("/{username}")
    public ResponseEntity<SuccessResponse<Void>>
    deleteUser(@PathVariable String username){
        userService.deleteUser(username);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Usuário excluido com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Código de verificação do e-mail",
            description = "Envio do código de verificação do e-mail"
    )
    @ApiResponse(responseCode = "200", description = "Código enviado com sucesso.")
    @SelfOrModError
    @UsernameErrors
    @PostMapping("/{username}/send-email-confirmation")
    public ResponseEntity<SuccessResponse<Void>> sendConfirmationEmail(@PathVariable String username){
        userService.sendConfirmationEmail(username);

        SuccessResponse<Void> response =
                new SuccessResponseBuilder<Void>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Token enviado com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Mudança de senha",
            description = "Mudança de senha"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Requester não é o target",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Nova senha igual antiga, senha nova e confirmação não conferem",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SelfOrModError
    @UsernameErrors
    @PatchMapping("/{username}/change-password")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    changePassword(@PathVariable String username, @RequestBody AuthChangePasswordRequest changePasswordRequest){
        AuthResponse data = userService.changePassword(username, changePasswordRequest);

        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("PATCH")
                        .code(HttpStatus.OK)
                        .message("Senha alterada com sucesso.")
                        .data(data)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
