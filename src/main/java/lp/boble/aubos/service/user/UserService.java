package lp.boble.aubos.service.user;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.pages.CustomInvalidPageException;
import lp.boble.aubos.mapper.user.UserMapper;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.response.pages.PageResponse;
import lp.boble.aubos.service.jwt.TokenService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthUtil authUtil;




    /**
     * Retorna todas as informações de um usuário
     * @param username target (em formato String)
     * @return {@link UserResponse}
     * @throws CustomNotFoundException quando: <br>
     * * Usuário não encontrado.
     * */
    public UserResponse getUserInfo(String username){

        this.checkUsernameAndPermission(username);

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

        this.validateSearchRequest(query, page);

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

        this.validateSearchRequest(query, page);

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

        this.checkUsernameAndPermission(username);

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

        this.checkUsernameAndPermission(username);

        UserModel userToDelete = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        userToDelete.setSoftDeleted(true);

        userRepository.save(userToDelete);
    }

    /**
     * Checa se o username está vázio e se o requester têm permissão para ação.
     *
     * @param username Username do usuário target da ação (em formato String)
     * @throws CustomFieldNotProvided quando: <br>
     *  * Username está vázio; <br>
     * @throws CustomForbiddenActionException quando: <br>
     *  * Não é uma Self Request ou não é um MOD.
     * */
    private void checkUsernameAndPermission(String username){
        if(username.isBlank()){
            throw CustomFieldNotProvided.username();
        }

        if(authUtil.isNotSelfOrAdmin(username)){
            throw CustomForbiddenActionException.notSelfOrAdmin();
        }
    }

    /**
     * Valida se os parametros para a busca estão válidas.
     *
     * @param query Query de busca (em formato String)
     * @param page Valor da página que o usuário quer acessar (em formato int)
     * @throws CustomFieldNotProvided quando: <br>
     * * Query está vázia. <br>
     * @throws CustomInvalidPageException quando: <br>
     * * Page é menor que 0.
     * */
    private void validateSearchRequest(String query, int page){
        if(query.isBlank()){
            throw CustomFieldNotProvided.query();
        }

        if(page < 0){
            throw new CustomInvalidPageException();
        }
    }

}
