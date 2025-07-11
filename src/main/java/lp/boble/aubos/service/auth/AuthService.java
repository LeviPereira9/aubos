package lp.boble.aubos.service.auth;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.auth.*;
import lp.boble.aubos.exception.custom.auth.CustomPasswordException;
import lp.boble.aubos.exception.custom.auth.CustomTokenException;
import lp.boble.aubos.exception.custom.global.CustomDeactivatedException;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.user.UserMapper;
import lp.boble.aubos.model.auth.TokenModel;
import lp.boble.aubos.model.auth.TokenTypeModel;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.repository.auth.TokenRepository;
import lp.boble.aubos.repository.user.UserRepository;
import lp.boble.aubos.service.email.EmailService;
import lp.boble.aubos.service.jwt.TokenService;
import lp.boble.aubos.util.AuthUtil;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final AuthUtil authUtil;


    private TokenTypeModel resetTypeToken;
    private TokenTypeModel emailTypeToken;

    @PostConstruct
    private void initTokenTypes(){
        resetTypeToken = new TokenTypeModel();
        resetTypeToken.setId(1L);

        emailTypeToken = new TokenTypeModel();
        emailTypeToken.setId(2L);
    }


    /**
     * Autentica o usuário
     * @param loginRequest - DTO com ‘login’ e senha (em formato {@link AuthLoginRequest}
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

    public void globalLogout(String username){
        authUtil.isNotSelfOrAdmin(username);

        UserModel target = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        target.setTokenId(UUID.randomUUID());
        userRepository.save(target);
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

        user.setTokenId(UUID.randomUUID());
        UserModel createdUser = userRepository.save(user);

        this.sendConfirmationEmail(createdUser);

        return new AuthResponse("Bearer " + tokenService.generateToken(createdUser));
    }

    public void forgotPassword(AuthForgotPasswordRequest forgotPasswordRequest){
        UserModel user;

        if(forgotPasswordRequest.login().contains("@")){
            user = userRepository.findByEmail(forgotPasswordRequest.login())
                    .orElseThrow(CustomNotFoundException::user);
        } else {
            user = userRepository.findByUsername(forgotPasswordRequest.login())
                    .orElseThrow(CustomNotFoundException::user);
        }

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                this.createToken(user, 15, resetTypeToken));

    }

    public void sendConfirmationEmail(UserModel user){
        emailService.sendVerifyEmail(
                user.getEmail(),
                this.createToken(user, 60, emailTypeToken)
        );
    }

    @Transactional
    public void changePassword(
            String token,
            AuthChangePasswordRequest changePasswordRequest){

        String newPassword = changePasswordRequest.newPassword();
        String confirmPassword = changePasswordRequest.confirmPassword();

        if(!newPassword.equals(confirmPassword)){
            throw CustomPasswordException.dontMatch();
        }

        // 1L = ResetToken
        TokenModel resetToken = tokenRepository.findByToken(token, resetTypeToken.getId())
                .orElseThrow(CustomNotFoundException::token);

        UserModel user = userRepository.findById(resetToken.getUser().getId())
                .orElseThrow(CustomNotFoundException::user);

        user.setTokenId(UUID.randomUUID());

        String encryptedPassword = new BCryptPasswordEncoder()
                .encode(changePasswordRequest.newPassword());

        user.setPasswordHash(encryptedPassword);
        resetToken.setUsed(true);
        resetToken.setUpdatedAt(Instant.now());

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }
    @Transactional
    public void confirmEmailVerification(String token){

        TokenModel emailToken = tokenRepository.findByToken(token, emailTypeToken.getId())
                .orElseThrow(CustomNotFoundException::token);

        UserModel user = userRepository.findById(emailToken.getUser().getId())
                .orElseThrow(CustomNotFoundException::user);

        user.setIsVerified(true);
        emailToken.setUsed(true);
        emailToken.setUpdatedAt(Instant.now());

        userRepository.save(user);
        tokenRepository.save(emailToken);
    }

    public void validateToken(String token, Long type){
        boolean valid = tokenRepository.isPending(token, type);

        if(!valid){
            throw CustomTokenException.invalid();
        }
    }

    private String createToken(
            UserModel user,
            int minutesToExpire,
            TokenTypeModel type){

        String tokenGenerated;

        do{
            tokenGenerated = generateToken();
        }while(tokenRepository.alreadyUsed(tokenGenerated, type.getId()));

        TokenModel token = new TokenModel();
        token.setToken(tokenGenerated);
        token.setUser(user);
        token.setExpiresAt(this.generateExpirationDate(minutesToExpire));
        token.setType(type);
        token.setCreatedAt(Instant.now());

        tokenRepository.save(token);

        return tokenGenerated;
    }

    private String generateToken(){
        SecureRandom random = new SecureRandom();

        int code = 100000+random.nextInt(900000);

        return Integer.toString(code);
    }

    private Instant generateExpirationDate(int minutes){
        return LocalDateTime.now().plusMinutes(minutes).toInstant(ZoneOffset.of("-03"));
    }

    @Transactional
    @Scheduled(cron = "0 */15 * * * *")
    public void disableTokens(){
        tokenRepository.disableToken(Instant.now());
    }

}
