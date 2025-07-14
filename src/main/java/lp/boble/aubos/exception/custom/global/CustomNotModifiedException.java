package lp.boble.aubos.exception.custom.global;

public class CustomNotModifiedException extends GlobalException {
    public CustomNotModifiedException(String message) {
        super(message);
    }

    public static CustomNotModifiedException user() {
        return new CustomNotModifiedException("Usuário não modificado.");
    }

    public static CustomNotModifiedException userQuery(){
        return new CustomNotModifiedException("Busca de usuários não modificada");
    }

    public static CustomNotModifiedException apiKey(){
        return new CustomNotModifiedException("Chaves não modificadas.");
    }
}
