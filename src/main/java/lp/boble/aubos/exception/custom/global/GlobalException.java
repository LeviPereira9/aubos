package lp.boble.aubos.exception.custom.global;

public abstract class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
}
