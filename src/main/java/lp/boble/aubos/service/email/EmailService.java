package lp.boble.aubos.service.email;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.email.CustomEmailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void simpleEmail(String email, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e)   {
            throw CustomEmailException.failed();
        }
    }

    /**
     * Envia o código de confirmação de e-mail, para o e-mail do sujeito.
     * @param email do usuário
     * @param token de ativação
     * @throws CustomEmailException Em caso de: <br>
     * - Algum erro no envio.
     *
     * */
    public void sendVerifyEmail(String email, String token){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Aubos]: Verificação de E-mail.");
            message.setText(String.format("Código para confirmação do e-mail: %s", token));
            mailSender.send(message);
        } catch (Exception e){
            throw CustomEmailException.failed();
        }
    }

    /**
     * Envia o código de reset de senha, para o e-mail do sujeito.
     * @param email do usuário
     * @param token de ativação
     * @throws CustomEmailException Em caso de: <br>
     * - Algum erro no envio.
     *
     * */
    public void sendPasswordResetEmail(String email, String token){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Aubos]: Recuperação de senha.");
            message.setText(String.format("Código para recuperação de senha: %s", token));
            mailSender.send(message);
        } catch (Exception e){
            throw CustomEmailException.failed();
        }
    }

}
