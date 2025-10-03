package lp.boble.aubos.exception.custom.global;

import lp.boble.aubos.exception.custom.user.UserException;

public class CustomDuplicateFieldException extends GlobalException {

    public CustomDuplicateFieldException(String message) {
        super(message);
    }

    public static CustomDuplicateFieldException username() {
        return new CustomDuplicateFieldException("Este username já está em uso.");
    }

    public static CustomDuplicateFieldException email() {
        return new CustomDuplicateFieldException("Este e-mail já está em uso.");
    }

    public static CustomDuplicateFieldException password() {
        return new CustomDuplicateFieldException("A nova senha não pode ser a mesma que a antiga.");
    }

    public static CustomDuplicateFieldException contributorName(){
        return new CustomDuplicateFieldException("O nome desse contribuidor já está em uso.");
    }

    public static CustomDuplicateFieldException bookFamily(){
        return new CustomDuplicateFieldException("Este livro já está na coleção.");
    }

    public static CustomDuplicateFieldException orderFamily(){
        return new CustomDuplicateFieldException("Outro livro já ocupa está posição.");
    }

    public static CustomDuplicateFieldException tag() {
        return new CustomDuplicateFieldException("Essa tag já existe.");
    }

    public static CustomDuplicateFieldException bookTag() {
        return new CustomDuplicateFieldException("Esse livro já possui esta tag.");
    }

    public static CustomDuplicateFieldException bookAlternativeTitle() {
        return new CustomDuplicateFieldException("Esse livro já possui este título alternativo.");
    }
}
