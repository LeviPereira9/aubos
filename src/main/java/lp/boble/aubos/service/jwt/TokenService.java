package lp.boble.aubos.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lp.boble.aubos.exception.custom.auth.CustomTokenException;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;


    /**
     * Gera o Token JWT
     * @param user {@link UserModel}
     * <hr>
     * @throws CustomTokenException Em caso de: <br>
     * - Falha na criação do Token
     * */
    public String generateToken(UserModel user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("aubos-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(generateExpirationDate())
                    .withClaim("id", user.getTokenId().toString())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw CustomTokenException.errorOnCreate();
        }
    }

    /**
     * Valida o Token do usuário e retorna o payload
     * @param token (String)
     * @return {@link TokenPayload}
     * <hr>
     * @throws CustomTokenException Em caso de:
     * - Falha na verificação do Token.
     * */
    public TokenPayload validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("aubos-api")
                    .build()
                    .verify(token);

            String username = decodedJWT.getSubject();
            String tokenId = decodedJWT.getClaim("id").asString();

            return new TokenPayload(username, tokenId);

        } catch (JWTVerificationException e) {
            throw CustomTokenException.errorOnValid();
        }
    }

    /**
     * Gera a expiração do Token JWT
     * @return 31 dias.
     * */
    public Instant generateExpirationDate(){
        return LocalDateTime.now().plusDays(31).toInstant(ZoneOffset.of("-03"));
    }

}
