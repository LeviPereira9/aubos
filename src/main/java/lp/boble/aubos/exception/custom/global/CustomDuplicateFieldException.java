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

}
