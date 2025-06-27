package lp.boble.aubos.dto.auth;

public record AuthChangePasswordRequest(
        String newPassword,
        String confirmPassword
) {}
