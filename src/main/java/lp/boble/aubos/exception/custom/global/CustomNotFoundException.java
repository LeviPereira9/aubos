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

    public static CustomNotFoundException family(){
        return new CustomNotFoundException("Nenhuma saga, série ou coletânea encontrada com esse nome.");
    }

    public static CustomNotFoundException familyType(){
        return new CustomNotFoundException("Tipo de coleção não encontrado.");
    }

    public static CustomNotFoundException visibility(){
        return new CustomNotFoundException("Classificação de visibilidade da coleção não encontrado.");
    }

    public static CustomNotFoundException bookFamily(){
        return new CustomNotFoundException("O livro não foi encontrado na coleção.");
    }

    public static CustomNotFoundException bookContributor(String role){
        return new CustomNotFoundException("Este livro não possuí nenhum contribuidor no cargo de " + role);
    }

    public static CustomNotFoundException bookContributor(){
        return new CustomNotFoundException("Este livro não possuí nenhum contribuidor.");
    }

    public static CustomNotFoundException tag() {
        return new CustomNotFoundException("Tag não encontrada.");
    }

    public static CustomNotFoundException bookTag() {
        return new CustomNotFoundException("Nenhuma tag foi encontrada para este livro.");
    }

    public static CustomNotFoundException alternativeTitle() {
        return new CustomNotFoundException("Título alternativo não encontrado.");
    }

    public static CustomNotFoundException license() {
        return new CustomNotFoundException("Licença não encontrada.");
    }
}
