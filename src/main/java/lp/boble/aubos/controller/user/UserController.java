package lp.boble.aubos.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.config.docSnippets.SelfOrModError;
import lp.boble.aubos.config.docSnippets.UsernameErrors;
import lp.boble.aubos.config.documentation.user.*;
import lp.boble.aubos.dto.auth.AuthChangePasswordRequest;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Tag(
        name = "User",
        description = "Endpoint de gerenciamento do usuário, incluindo registro, login, atualização, exclusão e busca de usuários."
)
@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @DocGetUserInfo
    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponse<UserResponse>> getUserInfo(
            @PathVariable String username,
            HttpServletRequest request){

        String eTag = this.generateUserEtag(username);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        UserResponse responseData = userService.getUserInfo(username);

        SuccessResponse<UserResponse> response =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();


        return ResponseEntity.ok()
                .eTag(eTag)
                .cacheControl(CacheProfiles.userPrivate())
                .body(response);
    }


    @DocGetUserShortInfo
    @GetMapping("/{username}/details")
    public ResponseEntity<SuccessResponse<UserShortResponse>>
    getUserShortInfo(@PathVariable String username, HttpServletRequest request){

        String eTag = this.generateUserEtag(username);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        UserShortResponse responseData = userService.getUserShortInfo(username);

        SuccessResponse<UserShortResponse> response =
                new SuccessResponseBuilder<UserShortResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.userPublic())
                .eTag(eTag)
                .body(response);
    }

    @DocGetAutoCompleteUser
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserAutocompleteProjection>>
    getAutoCompleteUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request){

        String eTag = this.generateQueryEtag(query, page);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        PageResponse<UserAutocompleteProjection> response =
                userService.getUserAutocomplete(query, page);

        return ResponseEntity.ok().eTag(eTag).body(response);
    }

    @DocGetSuggestionsUser
    @GetMapping("/suggestions")
    public ResponseEntity<PageResponse<UserSuggestionProjection>>
    getSuggestionsUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request){

        String eTag = this.generateQueryEtag(query, page);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if(eTag.equals(ifNoneMatch)){
            throw new CustomNotModifiedException();
        }

        PageResponse<UserSuggestionProjection> response =
                userService.getUserSuggestion(query, page);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DocUpdateUser
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


    @DocDeleteUser
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

    @DocSendConfirmationEmail
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

    @DocChangePassword
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

    private String generateUserEtag(String username){

        Instant lastUpdate = userRepository.getUpdate(username)
                .orElseThrow(CustomNotFoundException::user);

        return "\""+ lastUpdate.toEpochMilli() + "\"";
    }

    private String generateQueryEtag(String query, int page){
        String toHash = "query="+query.toLowerCase()+"&page="+page;

        return DigestUtils.md5DigestAsHex(toHash.getBytes(StandardCharsets.UTF_8));
    }
}
