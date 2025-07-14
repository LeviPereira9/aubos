package lp.boble.aubos.exception.custom.apikey;

// TODO: Exceptions
public class CustomApiKeyValidationException extends ApiKeyException {
    public CustomApiKeyValidationException(String message) {
        super(message);
    }

    public static CustomApiKeyValidationException expired() {
      return new CustomApiKeyValidationException("Chave expirada.");
    }

    public static CustomApiKeyValidationException rateLimit() {
      return new CustomApiKeyValidationException("Limite de requisições atigindo");
    }

    public static CustomApiKeyValidationException keyLimitExceeded() {
      return new CustomApiKeyValidationException("Limite de chaves atingindo.");
    }
}
