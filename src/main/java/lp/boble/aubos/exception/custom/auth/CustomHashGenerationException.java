package lp.boble.aubos.exception.custom.auth;

public class CustomHashGenerationException extends AuthException {
    public CustomHashGenerationException(String message) {
        super(message);
    }

    public static CustomHashGenerationException failedToGenerateHash() {
        return new CustomHashGenerationException("Falha na geração de hash.");
    }
}
