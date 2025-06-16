package lp.boble.aubos.controller.user;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.response.success.SuccessResponse;
import lp.boble.aubos.response.success.SuccessResponseBuilder;
import lp.boble.aubos.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserAuthResponse>>
    registerUser(@RequestBody UserRegisterRequest registerRequest) {

        UserAuthResponse responseData = userService.register(registerRequest);

        SuccessResponse<UserAuthResponse> response =
                new SuccessResponseBuilder<UserAuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.CREATED)
                        .message("Usuário registrado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<UserAuthResponse>>
    login(@RequestBody UserLoginRequest loginRequest) {

        UserAuthResponse responseData = userService.login(loginRequest);


        SuccessResponse<UserAuthResponse> response =
                new SuccessResponseBuilder<UserAuthResponse>()
                        .operation("POST")
                        .code(HttpStatus.OK)
                        .message("Usuário logado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponse<UserResponse>>
    getUserInfo(@PathVariable String username){
        UserResponse responseData = userService.getUserInfo(username);

        SuccessResponse<UserResponse> successResponse =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();


        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }


    @GetMapping("/{username}/details")
    public ResponseEntity<SuccessResponse<UserShortResponse>>
    getUserShortInfo(@PathVariable String username){
        UserShortResponse responseData = userService.getUserShortInfo(username);

        SuccessResponse<UserShortResponse> successResponse =
                new SuccessResponseBuilder<UserShortResponse>()
                        .operation("GET")
                        .code(HttpStatus.OK)
                        .message("Usuário encontrado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserAutocompleteProjection>>
    getAutocompleteUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<UserAutocompleteProjection> successResponse =
                userService.getUserAutocomplete(query, page);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<PageResponse<UserSuggestionProjection>>
    getSuggestionsUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page){

        PageResponse<UserSuggestionProjection> successResponse =
                userService.getUserSuggestion(query, page);

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PutMapping("/{username}")

    public ResponseEntity<SuccessResponse<UserResponse>>
    updateUser(@PathVariable String username,
               @RequestBody UserUpdateRequest updateRequest){

        UserResponse responseData = userService.updateUser(username, updateRequest);

        SuccessResponse<UserResponse> successResponse =
                new SuccessResponseBuilder<UserResponse>()
                        .operation("PUT")
                        .code(HttpStatus.OK)
                        .message("Usuário atualizado com sucesso.")
                        .data(responseData)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<SuccessResponse<Void>>
    deleteUser(@PathVariable String username){
        userService.deleteUser(username);

        SuccessResponse<Void> successResponse =
                new SuccessResponseBuilder<Void>()
                        .operation("DELETE")
                        .code(HttpStatus.OK)
                        .message("Usuário excluido com sucesso.")
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}
