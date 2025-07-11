package lp.boble.aubos.exception.custom.email;

public abstract class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
