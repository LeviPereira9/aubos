package lp.boble.aubos.exception.custom.email;

public class CustomEmailException extends RuntimeException {
    public CustomEmailException(String message) {
        super(message);
    }

    public static CustomEmailException failed() {
        return new CustomEmailException("Falha no envio de e-mail");
    }
}
