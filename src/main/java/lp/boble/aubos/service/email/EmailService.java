package lp.boble.aubos.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendToken(String email, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
