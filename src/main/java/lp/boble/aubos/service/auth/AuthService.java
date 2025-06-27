package lp.boble.aubos.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.*;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.user.UserMapper;
import lp.boble.aubos.model.auth.ResetToken;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.auth.ResetTokenRepository;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.service.email.EmailService;
import lp.boble.aubos.service.jwt.TokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final ResetTokenRepository tokenRepository;

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

    public void forgotPassword(AuthForgotPasswordRequest forgotPasswordRequest){
        UserModel user;

        if(forgotPasswordRequest.login().contains("@")){
            user = userRepository.findByEmail(forgotPasswordRequest.login())
                    .orElseThrow(CustomNotFoundException::user);
        } else {
            user = userRepository.findByUsername(forgotPasswordRequest.login())
                    .orElseThrow(CustomNotFoundException::user);;
        }

        String emailText = String.format("Aqui está o código de confirmação para recuperação de senha: %s", this.createUserToken(user));

        emailService.sendToken(
                user.getEmail(),
                "Recuperação de Senha",
                emailText
        );

    }

    private String createUserToken(UserModel user){
        String token;

        do{
            token = generateToken();
        } while(tokenRepository.existsByTokenAndUsedTrue(token));

        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiresAt(this.generateExpirationDate());
        resetToken.setCreatedAt(Instant.now());

        tokenRepository.save(resetToken);

        return token;
    }

    private String generateToken(){
        SecureRandom random = new SecureRandom();

        int code = 100000+random.nextInt(900000);

        return Integer.toString(code);
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03"));
    }

    public boolean validateResetToken(String resetToken){

        return tokenRepository.existsByTokenAndUsedFalse(resetToken);
    }

    @Transactional
    public void changePassword(String token, AuthChangePasswordRequest changePasswordRequest){
        if(!validateResetToken(token)){
            throw new RuntimeException("Token inválido");
        }

        if(!changePasswordRequest.newPassword().equals(changePasswordRequest.confirmPassword())){
            throw new RuntimeException("Senhas incompátiveis");
        }

        ResetToken resetToken = tokenRepository.findByToken(token);
        UserModel user = userRepository.findById(resetToken.getUser().getId()).orElseThrow(CustomNotFoundException::user);;

        String encryptedPassword = new BCryptPasswordEncoder()
                .encode(changePasswordRequest.newPassword());

        user.setPasswordHash(encryptedPassword);
        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }

    @Transactional
    @Scheduled(cron = "0 */10 * * * *")
    public void disableTokens(){
        tokenRepository.disableToken(Instant.now());
    }

}
