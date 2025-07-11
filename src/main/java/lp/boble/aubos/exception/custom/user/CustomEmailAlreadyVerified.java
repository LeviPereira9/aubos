package lp.boble.aubos.exception.custom.user;

public class CustomEmailAlreadyVerified extends UserException {
    public CustomEmailAlreadyVerified() {
        super("Esse e-mail já foi verificado.");
    }
}
