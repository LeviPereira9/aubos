package lp.boble.aubos.exception.custom.user;

public abstract class UserException extends RuntimeException{

    UserException(String message) {
        super(message);
    }
}
