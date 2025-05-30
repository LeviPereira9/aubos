package lp.boble.aubos.exception.custom.auth;

public abstract class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
