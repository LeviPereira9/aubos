package lp.boble.aubos.util;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.pages.CustomInvalidPageException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final AuthUtil authUtil;
    /**
     * Checa se o username está vázio e se o requester têm permissão para ação.
     *
     * @param username Username do usuário target da ação (em formato String)
     * @throws CustomFieldNotProvided quando: <br>
     *  * Username está vázio; <br>
     * @throws CustomForbiddenActionException quando: <br>
     *  * Não é uma Self Request ou não é um MOD.
     * */
    public void checkUsernameAndPermission(String username){
        if(username.isBlank()){
            throw CustomFieldNotProvided.username();
        }

        authUtil.requestIsNotSelfOrByAdmin(username);
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
    public void validateSearchRequest(String query, int page){
        if(query.isBlank()){
            throw CustomFieldNotProvided.query();
        }

        if(page < 0){
            throw new CustomInvalidPageException();
        }
    }

}
