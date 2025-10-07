package lp.boble.aubos.service.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.email.CustomEmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private void sendEmail(String email, String subject, String text) {
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

    @Async
    public void sendVerifyEmail(String email, String token){
        String subject = "[Aubos]: Verificação de E-mail.";
        String text = String.format("Código para confirmação do e-mail: %s", token);

        this.sendEmail(email, subject, text);
    }

    @Async
    public void sendPasswordResetEmail(String email, String token){
            String subject = "[Aubos]: Recuperação de senha.";
            String text = String.format("Código para recuperação de senha: %s", token);
            this.sendEmail(email, subject, text);
    }

}
