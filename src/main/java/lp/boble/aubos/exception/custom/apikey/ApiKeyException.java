package lp.boble.aubos.exception.custom.apikey;

public abstract class ApiKeyException extends RuntimeException{

    public ApiKeyException(String message) {
        super(message);
    }
}
