package project.mailer;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        // Чтение содержимого файла signature.html
        String htmlContent = readHtmlFile("signature.html");

        // Установка текста письма с HTML-подписью
        helper.setText(text + "<br/><br/>" + htmlContent, true);

        message.setFrom(new InternetAddress(props.getProperty("spring.mail.username")));

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
        if (props.getProperty("copy.items.to.sent.folder").equals("true")) {
            saveToSentFolder(message, props);
        }
    }

    private void saveToSentFolder(MimeMessage message, Properties props) {
        try {
            Properties imapProps = new Properties();
            imapProps.put("mail.store.protocol", props.getProperty("spring.mail.store.protocol"));
            imapProps.put("mail.imaps.host", props.getProperty("spring.mail.host"));
            imapProps.put("mail.imaps.port", props.getProperty("spring.mail.imaps.port"));
            imapProps.put("mail.imaps.ssl.enable", props.getProperty("spring.mail.imaps.ssl.enable"));

            Session session = Session.getInstance(imapProps);
            Store store = session.getStore(props.getProperty("spring.mail.store.protocol"));
            store.connect(props.getProperty("spring.mail.host"), props.getProperty("spring.mail.username"), props.getProperty("spring.mail.password"));

            Folder sentFolder = store.getFolder(props.getProperty("spring.mail.store.sent.folder")); // заменить путь к папке в зависимости от почтового сервиса
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }
            sentFolder.open(Folder.READ_WRITE);
            sentFolder.appendMessages(new Message[]{message});

            sentFolder.close(false);
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Метод для чтения содержимого HTML-файла
    private String readHtmlFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Файл " + filePath + " не найден");
        }
        return FileUtils.readFileToString(file, StandardCharsets.ISO_8859_1);
    }
}