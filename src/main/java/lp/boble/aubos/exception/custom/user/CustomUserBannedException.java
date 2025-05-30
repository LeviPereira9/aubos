package lp.boble.aubos.exception.custom.user;

public class CustomUserBannedException extends UserException {
    public CustomUserBannedException() {
        super("Está conta foi banida permanentemente.");
    }

    public CustomUserBannedException(String message) {
        super(message);
    }
}
