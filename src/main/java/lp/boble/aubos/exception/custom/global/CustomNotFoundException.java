package lp.boble.aubos.exception.custom.global;

import lp.boble.aubos.exception.custom.user.UserException;

public class CustomNotFoundException extends GlobalException {

    public CustomNotFoundException(String message) {
        super(message);
    }

    public static CustomNotFoundException user() {
        return new CustomNotFoundException("Usuário não encontrado.");
    }

    public static CustomNotFoundException key(){
        return new CustomNotFoundException("Chave não encontrada.");
    }

    public static CustomNotFoundException keyOwner(){
        return new CustomNotFoundException("O dono desta chave ");
    }

    public static CustomNotFoundException token(){
        return new CustomNotFoundException("TokenModel não encontrado.");
    }

    public static CustomNotFoundException book(){
        return new CustomNotFoundException("Livro não encontrado.");
    }

    public static CustomNotFoundException language(){
        return new CustomNotFoundException("Língua não encontrada.");
    }
}
