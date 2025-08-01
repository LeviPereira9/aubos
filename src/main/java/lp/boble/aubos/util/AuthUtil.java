package lp.boble.aubos.util;

import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    UserModel requester;

    /**
     * @return UserModel - retorna o usuário que está fazendo a requisição.
     * */
    public UserModel getRequester() {

        if(this.requester != null){
            return requester;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            return null;
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof UserModel user){
            this.requester = user;
            return user;
        }

        return null;
    }


    /**
     * @param username - Username do target  (em formato String)
     * @throws CustomFieldNotProvided em caso de:
     *  - Username vázio
     * @throws CustomForbiddenActionException em caso de:
     * - Não é o requester nem um ADMIN
     * */
    public void isNotSelfOrAdmin(String username){
        if(username.isBlank()){
            throw CustomFieldNotProvided.username();
        }

        UserModel requester = getRequester();

        boolean isOwner = username.equals(requester.getUsername());
        boolean isAdmin = isAdmin();

        if(!isOwner && !isAdmin) throw CustomForbiddenActionException.notSelfOrAdmin();

    }

    public boolean isAdmin(){
        return requester.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MOD"));
    }
}
