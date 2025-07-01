package lp.boble.aubos.dto.auth;

public record AuthTokenRequest(
        String token,
        Long type
) {}
