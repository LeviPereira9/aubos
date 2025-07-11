package lp.boble.aubos.service.user;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.AuthChangePasswordRequest;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.auth.CustomPasswordException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.pages.CustomInvalidPageException;
import lp.boble.aubos.exception.custom.user.CustomEmailAlreadyVerified;
import lp.boble.aubos.mapper.user.UserMapper;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.service.auth.AuthService;
import lp.boble.aubos.service.email.EmailService;
import lp.boble.aubos.service.jwt.TokenService;
import lp.boble.aubos.util.AuthUtil;
import lp.boble.aubos.util.ValidationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ValidationUtil validationUtil;
    private final AuthUtil authUtil;
    private final EmailService emailService;
    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * Retorna todas as informações de um usuário
     * @param username target (em formato String)
     * @return {@link UserResponse}
     * @throws CustomNotFoundException quando: <br>
     * * Usuário não encontrado.
     * */
    public UserResponse getUserInfo(String username){

        authUtil.isNotSelfOrAdmin(username);

        UserModel found = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        return userMapper.fromModelToResponse(found);
    }
    /**
     * Busca informações simples de usuário
     * @param username target (em formato String)
     * @return {@link UserShortResponse}
     * @throws CustomNotFoundException quando: <br>
     *  * Usuário não encontrado.
     * */
    public UserShortResponse getUserShortInfo(String username){
        if(username.isBlank()){
            throw CustomFieldNotProvided.username();
        }

        UserModel found = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        return userMapper.fromModelToShortResponse(found);
    }

    /**
     * Busca usuários devolvendo mais informações simples.
     * @param query username target (em formato de String)
     * @param page paginação (em formato int)
     * @return {@link PageResponse}<{@link UserAutocompleteProjection}>
     *
     * */
    public PageResponse<UserAutocompleteProjection> getUserAutocomplete(String query, int page){

        validationUtil.validateSearchRequest(query, page);

        PageRequest pageRequest = PageRequest.of(
                page,
                5);

        return new PageResponse<>(
                userRepository.findUserAutocomplete(query, pageRequest)
        );
    }

    /**
     * Busca usuários devolvendo mais informações relevantes.
     * @param query username target (em formato de String)
     * @param page paginação (em formato int)
     * @return {@link PageResponse}<{@link UserSuggestionProjection}>
     *
     * */
    public PageResponse<UserSuggestionProjection> getUserSuggestion(String query, int page){

        validationUtil.validateSearchRequest(query, page);

        PageRequest pageRequest = PageRequest.of(
                page,
                10
        );

        return new PageResponse<>(
                userRepository.findUserSuggestions(query, pageRequest)
        );
    }

    /**
     * Atualiza os dados do usuário.
     * @param username - identificador do target (em formato String)
     * @param request - campos DTO para atualização (em formato {@link UserUpdateRequest}
     * @return {@link UserResponse}
     * @throws CustomNotFoundException quando: <br>
     *  * Usuário não encontrado..
     *
     * */
    public UserResponse updateUser(String username, UserUpdateRequest request){

        authUtil.isNotSelfOrAdmin(username);

        UserModel found = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        userMapper.fromUpdateToModel(request, found);

        return userMapper.fromModelToResponse(userRepository.save(found));
    }

    /**
     * Soft delete em um usuário
     * @param username - identificação do target (em formato String)
     * @throws CustomNotFoundException quando: <br>
     * * Usuário não encontrado.
     * */
    public void deleteUser(String username){

        authUtil.isNotSelfOrAdmin(username);

        UserModel userToDelete = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        userToDelete.setSoftDeleted(true);

        userRepository.save(userToDelete);
    }

    public void sendConfirmationEmail(String username){
        authUtil.isNotSelfOrAdmin(username);

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        if(user.getIsVerified()){
            throw new CustomEmailAlreadyVerified();
        }

        authService.sendConfirmationEmail(user);
    }

    public AuthResponse changePassword(String username, AuthChangePasswordRequest changePasswordRequest){
        if(!authUtil.getRequester().getUsername().equals(username)){
            throw CustomForbiddenActionException.notTheRequester();
        }

        String newPassword = changePasswordRequest.newPassword();
        String confirmPassword = changePasswordRequest.confirmPassword();

        if(!newPassword.equals(confirmPassword)){
            throw CustomPasswordException.sameAsCurrent();
        }

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw CustomPasswordException.dontMatch();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setTokenId(UUID.randomUUID());

        userRepository.save(user);

        return new AuthResponse("Bearer " + tokenService.generateToken(user));
    }



}
