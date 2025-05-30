package lp.boble.aubos.exception.custom.apikey;

public class CustomApiKeyGenerationException extends ApiKeyException{
    public CustomApiKeyGenerationException(String message) {
        super(message);
    }

    public static CustomApiKeyGenerationException generateHash() {
      return new CustomApiKeyGenerationException("Falha na criação da chave, tente novamente mais tarde.");
    }
}
