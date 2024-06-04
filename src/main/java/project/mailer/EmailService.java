package project.mailer;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text, String attachmentPath) throws MessagingException, IOException {

        //Resource resource = new ClassPathResource("../mail-config.txt");
        Resource resource = new FileSystemResource("mail-config.txt");

        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        Properties props = PropertiesLoaderUtils.loadProperties(encodedResource);

        String user = props.getProperty("spring.mail.username");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        //helper.setText(text, true);

        // Установка флага важности
        message.setHeader("X-Priority", "1");
        message.setHeader("X-MSMail-Priority", "High");
        message.setHeader("Importance", "High");

        // Установка заголовков для подтверждения доставки и прочтения
        message.setHeader("Return-Receipt-To", user);
        message.setHeader("Disposition-Notification-To", user);


        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            File file = new File("attachments/" + attachmentPath);
            if (file.exists() && file.isFile()) {
                helper.addAttachment(file.getName(), file);
            } else {
                System.err.println("Attachment file not found: " + attachmentPath);
            }
        }

        mailSender.send(message);
    }
}