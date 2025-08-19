package lp.boble.aubos.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.config.cache.CacheProfiles;
import lp.boble.aubos.config.documentation.user.*;
import lp.boble.aubos.dto.auth.AuthChangePasswordRequest;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.exception.custom.global.CustomNotModifiedException;
import lp.boble.aubos.repository.user.UserRepository;
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

        UserResponse content = userService.getUserInfo(username);

        SuccessResponse<UserResponse> response =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .content(content)
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

        UserShortResponse content = userService.getUserShortInfo(username);

        SuccessResponse<UserShortResponse> response =
                new SuccessResponseBuilder<UserShortResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.userPublic())
                .eTag(eTag)
                .body(response);
    }

    @DocGetAutoCompleteUser
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserAutocompletePageResponse>>
    getAutoCompleteUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<UserAutocompletePageResponse> response = userService.getUserAutocomplete(query, page);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

    @DocGetSuggestionsUser
    @GetMapping("/suggestions")
    public ResponseEntity<PageResponse<UserSuggestionPageResponse>>
    getSuggestionsUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){


        PageResponse<UserSuggestionPageResponse> response =
                userService.getUserSuggestion(query, page);

        return ResponseEntity.ok()
                .cacheControl(CacheProfiles.searchFieldPublic())
                .body(response);
    }

    @DocUpdateUser
    @PutMapping("/{username}")
    public ResponseEntity<SuccessResponse<UserResponse>>
    updateUser(@PathVariable String username,
               @RequestBody UserUpdateRequest updateRequest){

        UserResponse content = userService.updateUser(username, updateRequest);

        SuccessResponse<UserResponse> response =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Usuário atualizado com sucesso.")
                        .content(content)
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

    @DocUserChangePassword
    @PatchMapping("/{username}/change-password")
    public ResponseEntity<SuccessResponse<AuthResponse>>
    changePassword(@PathVariable String username, @RequestBody AuthChangePasswordRequest changePasswordRequest){
        AuthResponse content = userService.changePasswordAndGenerateAuthToken(username, changePasswordRequest);

        SuccessResponse<AuthResponse> response =
                new SuccessResponseBuilder<AuthResponse>()
                        .operation("PATCH")
                        .code(HttpStatus.OK)
                        .message("Senha alterada com sucesso.")
                        .content(content)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private String generateUserEtag(String username){

        Instant lastUpdate = userRepository.getUpdate(username)
                .orElse(null);

        String base = (lastUpdate != null)
                ? lastUpdate.toString()
                : "no-update" + username;

        return "\"" + DigestUtils.md5DigestAsHex(base.getBytes(StandardCharsets.UTF_8)) + "\"";
    }

    // TODO: eTag digno
    private String generateQueryEtag(String query, int page){
        String toHash = "query="+query.toLowerCase()+"&page="+page;

        return DigestUtils.md5DigestAsHex(toHash.getBytes(StandardCharsets.UTF_8));
    }
}
