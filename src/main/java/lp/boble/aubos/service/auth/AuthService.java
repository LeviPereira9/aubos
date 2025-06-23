package lp.boble.aubos.service.auth;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.AuthResponse;
import lp.boble.aubos.dto.auth.AuthLoginRequest;
import lp.boble.aubos.dto.auth.AuthRegisterRequest;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.mapper.user.UserMapper;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.service.jwt.TokenService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    /**
     * Autentica o usuário
     * @param loginRequest - DTO com login e senha (em formato {@link AuthLoginRequest}
     * @return {@link AuthResponse} - token
     * */
    public AuthResponse login(AuthLoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.login(),
                        loginRequest.password()
                );

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        return new AuthResponse("Bearer " + tokenService.generateToken((UserModel) auth.getPrincipal()));

    }

    /**
     * Registra um novo usuário no banco de dados.
     * @param registerRequest - (em formato {@link AuthRegisterRequest}
     * @return {@link AuthResponse} - token.
     * @throws CustomDuplicateFieldException quando: <br>
     * * Username já está em uso. <br>
     * * E-mail já está em uso.
     *
     * */
    public AuthResponse register(AuthRegisterRequest registerRequest){

        if(userRepository.existsByUsername(registerRequest.username())){
            throw CustomDuplicateFieldException.username();
        }

        if(userRepository.existsByEmail(registerRequest.email())){
            throw CustomDuplicateFieldException.email();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.password());
        UserModel user = userMapper.fromRegisterToModel(registerRequest);
        user.setPasswordHash(encryptedPassword);

        UserModel createdUser = userRepository.save(user);

        return new AuthResponse("Bearer " + tokenService.generateToken(createdUser));
    }

}
