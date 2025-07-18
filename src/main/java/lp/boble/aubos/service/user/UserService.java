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

import java.time.Instant;
import java.time.LocalDateTime;
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
     * @throws CustomFieldNotProvided em caso de:
     *  - Username vázio
     * @throws CustomForbiddenActionException em caso de:
     * - Não é o requester nem um ADMIN
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
    @Cacheable(value = "userShort", key = "#username")
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
    @CachePut(value = "userShort", key = "#username")
    public UserResponse updateUser(String username, UserUpdateRequest request){

        authUtil.isNotSelfOrAdmin(username);

        UserModel found = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        userMapper.fromUpdateToModel(request, found);

        found.setUpdatedAt(Instant.now());

        return userMapper.fromModelToResponse(userRepository.save(found));
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

        authUtil.isNotSelfOrAdmin(username);

        UserModel userToDelete = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

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
        authUtil.isNotSelfOrAdmin(username);

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        if(user.getIsVerified()){
            throw new CustomEmailAlreadyVerified();
        }

        authService.sendConfirmationEmail(user);
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
