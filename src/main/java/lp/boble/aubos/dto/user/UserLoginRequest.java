package lp.boble.aubos.dto.user;

public record UserLoginRequest(
        String login,
        String password
) {
}
