package lp.boble.aubos.exception.custom.auth;

public class CustomPasswordException extends AuthException {
    public CustomPasswordException(String message) {
        super(message);
    }

    public static CustomPasswordException sameAsCurrent() {
        return new CustomPasswordException("A nova senha não pode ser igual a anterior.");
    }

    public static CustomPasswordException dontMatch(){
        return new CustomPasswordException("As senhas não semelhantes.");
    }
}
