package lp.boble.aubos.exception.custom.auth;

public class CustomTokenException extends AuthException {
    public CustomTokenException(String message) {
        super(message);
    }

    public static CustomTokenException invalid() {
        return new CustomTokenException("Token inválido.");
    }

    public static CustomTokenException errorOnValid() {
        return new CustomTokenException("Erro na validação do token.");
    }

    public static CustomTokenException errorOnCreate() {
        return new CustomTokenException("Erro na criação do token.");
    }
}
