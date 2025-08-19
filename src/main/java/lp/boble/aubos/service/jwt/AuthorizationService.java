package lp.boble.aubos.service.jwt;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserModel loadUserByUsername(String login){

        if(login.isBlank() || login.equals("NONE_PROVIDED")){
            throw CustomFieldNotProvided.login();
        }

        UserModel user = this.findUserByLogin(login);

        if(user.getSoftDeleted()){
            throw new CustomNotFoundException("Usuário inativo.");
        }

        return user;
    }

    private UserModel findUserByLogin(String login){
        Optional<UserModel> optionalUser;

        if(login.contains("@")){
            optionalUser = userRepository.findByEmail(login);
        } else {
            optionalUser = userRepository.findByUsername(login);
        }

        return optionalUser.orElseThrow(CustomNotFoundException::user);
    }
}
