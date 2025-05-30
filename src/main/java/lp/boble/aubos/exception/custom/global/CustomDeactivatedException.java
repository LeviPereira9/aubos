package lp.boble.aubos.exception.custom.global;

public class CustomDeactivatedException extends GlobalException {
    public CustomDeactivatedException(String message) {
        super(message);
    }

    public static CustomDeactivatedException user(){
        return new CustomDeactivatedException("Este usuário foi desativado.");
    }
    public static CustomDeactivatedException key(){
        return new CustomDeactivatedException("Esta chave foi desativada.");
    }

}
