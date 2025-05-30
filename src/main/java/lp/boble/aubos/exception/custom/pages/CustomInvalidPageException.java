package lp.boble.aubos.exception.custom.pages;

public class CustomInvalidPageException extends PagesException {
    public CustomInvalidPageException() {
        super("Paginação inválida.");
    }
}
