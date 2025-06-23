package lp.boble.aubos.dto.auth;

public record AuthLoginRequest(
        String login,
        String password
) {
}
