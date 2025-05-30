package lp.boble.aubos.exception.custom.global;

public class CustomFieldNotProvided extends GlobalException {


    public CustomFieldNotProvided(String message) {
        super(message);
    }

    public static CustomFieldNotProvided username() {
        return new CustomFieldNotProvided("Username não pode estar vázio.");
    }

    public static CustomFieldNotProvided query() {
        return new CustomFieldNotProvided("O campo de pesquisa não pode estar vázio.");
    }

    public static CustomFieldNotProvided key(){
        return new CustomFieldNotProvided("A chave fornecida está incorreta.");
    }

    public static CustomFieldNotProvided login(){
        return new CustomFieldNotProvided("Username/e-mail não podem estar vázios.");
    }
}
