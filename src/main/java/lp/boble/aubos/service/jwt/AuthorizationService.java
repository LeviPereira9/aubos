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
    public UserDetails loadUserByUsername(String usernameOrEmail){

        if(usernameOrEmail.isBlank() || usernameOrEmail.equals("NONE_PROVIDED")){
            throw CustomFieldNotProvided.login();
        }

        Optional<UserModel> optionalUser;

        if(usernameOrEmail.contains("@")){
            optionalUser = userRepository.findByEmail(usernameOrEmail);
        } else {
            optionalUser = userRepository.findByUsername(usernameOrEmail);
        }

        UserModel user = optionalUser.orElseThrow(CustomNotFoundException::user);

        if(user.getSoftDeleted()){
            throw new CustomNotFoundException("Usuário inativo.");
        }

        return user;
    }
}
