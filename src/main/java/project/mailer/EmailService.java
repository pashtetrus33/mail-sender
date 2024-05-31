package project.mailer;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text, String attachmentPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        //helper.setText(text, true);

        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            File file = new File(attachmentPath);
            if (file.exists() && file.isFile()) {
                helper.addAttachment(file.getName(), file);
            } else {
                System.err.println("Attachment file not found: " + attachmentPath);
            }
        }

        mailSender.send(message);
    }
}


