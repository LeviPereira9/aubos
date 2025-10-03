package lp.boble.aubos.exception.custom.auth;

public class CustomForbiddenActionException extends AuthException {
    public CustomForbiddenActionException(String message) {
        super(message);
    }

    public static CustomForbiddenActionException notSelfOrAdmin() {
      return new CustomForbiddenActionException("Você não têm autorização para está ação.");
    }

    public static CustomForbiddenActionException noToken() {
        return new CustomForbiddenActionException("Nenhum autenticador fornecido.");
    }

    public static CustomForbiddenActionException notTheRequester() {
        return new CustomForbiddenActionException("Apenas o próprio usuário pode realizar esta ação.");
    }

    public static CustomForbiddenActionException bookTagNotAssociated() {
        return new CustomForbiddenActionException("Essa tag não está associada a este livro.");
    }

    public static CustomForbiddenActionException alternativeTitleNotAssociated() {
        return new CustomForbiddenActionException("Esse título não está associado a este livro.");
    }
}
