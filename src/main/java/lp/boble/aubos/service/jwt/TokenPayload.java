package lp.boble.aubos.service.jwt;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class TokenPayload {
    String subject;
    String token;

    public TokenPayload(String subject, String token) {
        this.subject = subject;
        this.token = token;
    }
}
