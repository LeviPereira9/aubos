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

}
