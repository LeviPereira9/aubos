package lp.boble.aubos.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.AuthChangePasswordRequest;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.auth.CustomPasswordException;
import lp.boble.aubos.exception.custom.email.CustomEmailException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthUtil authUtil;
    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * Retorna todas as informações de um usuário
     * @param username target (em formato String)
     * @return {@link UserResponse}
     * @throws CustomNotFoundException quando: <br>
     * * Usuário não encontrado.
     * @throws CustomFieldNotProvided em caso de:
     *  - Username vázio
     * @throws CustomForbiddenActionException em caso de:
     * - Não é o requester nem um ADMIN
     * */
    public UserResponse getUserInfo(String username){

        UserModel userFound = this.findUserIfIsSelfOrAdmin(username);

        return userMapper.fromModelToResponse(userFound);
    }

    /**
     * Busca informações simples de usuário
     * @param username target (em formato String)
     * @return {@link UserShortResponse}
     * @throws CustomNotFoundException quando: <br>
     *  * Usuário não encontrado.
     * */
    @Cacheable(value = "userShort", key = "#username")
    public UserShortResponse getUserShortInfo(String username){

        UserModel userFound = this.findUser(username);

        return userMapper.fromModelToShortResponse(userFound);
    }

    /**
     * Busca usuários devolvendo mais informações simples.
     * @param search username target (em formato de String)
     * @param page paginação (em formato int)
     * @return {@link PageResponse}<{@link UserAutocompletePageProjection}>
     *
     * */
    @Cacheable(value = "userAutocomplete", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<UserAutocompletePageResponse> getUserAutocomplete(String search, int page){

        PageRequest pageRequest = PageRequest.of(
                page,
                5);

        PageResponse<UserAutocompletePageProjection> pageFound = new PageResponse<>(
                userRepository.findUserAutocomplete(search, pageRequest)
        );

        return pageFound.map(userMapper::fromAutocompleteProjectionToResponse);
    }

    /**
     * Busca usuários devolvendo mais informações relevantes.
     * @param search username target (em formato de String)
     * @param page paginação (em formato int)
     * @return {@link PageResponse}<{@link UserSuggestionPageProjection}>
     *
     * */
    @Cacheable(value = "userSearch", key = "'search=' + #search + ',page=' + #page")
    public PageResponse<UserSuggestionPageResponse> getUserSuggestion(String search, int page){

        PageRequest pageRequest = PageRequest.of(
                page,
                10
        );

        PageResponse<UserSuggestionPageProjection> pageFound = new PageResponse<>(
                userRepository.findUserSuggestions(search, pageRequest)
        );

        return pageFound.map(userMapper::fromSuggestionProjectionToResponse);
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
    @CachePut(value = "userShort", key = "#username")
    public UserResponse updateUser(String username, UserUpdateRequest request){

        UserModel userToUpdate = this.findUserIfIsSelfOrAdmin(username);

        userMapper.toUpdateFromRequest(userToUpdate, request);

        return userMapper.fromModelToResponse(userRepository.save(userToUpdate));
    }

    /**
     * Soft delete em um usuário
     * @param username - identificação do target (em formato String)
     * @throws CustomNotFoundException quando: <br>
     * * Usuário não encontrado.
     * */
    @CacheEvict(value = "userShort", key = "#username")
    @Transactional
    public void deleteUser(String username){

        UserModel userToDelete = this.findUserIfIsSelfOrAdmin(username);

        userToDelete.setSoftDeleted(true);

        userRepository.save(userToDelete);
    }

    /**
     * Envia o código de confirmação de e-mail, para o e-mail do sujeito.
     * @param username do usuário
     * @throws CustomEmailAlreadyVerified Em caso de: <br>
     * - E-mail já foi verificado.
     * @throws CustomEmailException Em caso de: <br>
     * - Algum erro no envio.
     * */
    public void sendConfirmationEmail(String username){

        UserModel userToSendEmail = this.findUserIfIsSelfOrAdmin(username);

        if(userToSendEmail.getIsVerified()){
            throw new CustomEmailAlreadyVerified();
        }

        authService.sendConfirmationEmail(userToSendEmail);
    }

    /**
     * Mudança de senha do usuário
     * @param username do usuário que vai receber a troca de senha
     * @param changePasswordRequest dto com a nova senha {@link AuthChangePasswordRequest}
     * @return {@link AuthResponse}
     * <hr>
     * @throws CustomForbiddenActionException Em caso de: <br>
     * - Requester não é o target
     * @throws CustomPasswordException Em caso de: <br>
     * - Senha nova é igual a antiga <br>
     * - Senha nova e confirmação de senha não conferem
     * @throws CustomNotFoundException Em caso de:<br>
     * - Usuário não encontrado
     * */
    @Transactional
    public AuthResponse changePasswordAndGenerateAuthToken(String username, AuthChangePasswordRequest changePasswordRequest){

        UserModel userToChangePassword = this.findUserIfIsSelfOrAdmin(username);

        String currentUserPasswordHash = userToChangePassword.getPasswordHash();

        String newPassword = this.validateAndEncodePassword(changePasswordRequest, currentUserPasswordHash);

        userToChangePassword.setNewPassword(newPassword);

        userRepository.save(userToChangePassword);

        return new AuthResponse("Bearer " + tokenService.generateToken(userToChangePassword));
    }

    private UserModel findUser(String username){
        authUtil.requestIsNotSelfOrByAdmin(username);

        return userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);
    }

    private UserModel findUserIfIsSelfOrAdmin(String username){
        authUtil.requestIsNotSelfOrByAdmin(username);

        return this.findUser(username);
    }

    private String validateAndEncodePassword(AuthChangePasswordRequest changePasswordRequest, String currentPasswordHash){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String newPassword = changePasswordRequest.newPassword();
        String confirmPassword = changePasswordRequest.confirmPassword();

        if(!newPassword.equals(confirmPassword)){
            throw CustomPasswordException.sameAsCurrent();
        }

        if(passwordEncoder.matches(changePasswordRequest.newPassword(), currentPasswordHash)){
            throw CustomPasswordException.dontMatch();
        }

        return passwordEncoder.encode(changePasswordRequest.newPassword());
    }


}
