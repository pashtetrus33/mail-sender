package project.mailer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class Config {
    @Bean
    @Primary
    public JavaMailSender getJavaMailSender() throws IOException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();

        //Resource resource = new ClassPathResource("../mail-config.txt");
        Resource resource = new FileSystemResource("mail-config.txt");

        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        Properties props = PropertiesLoaderUtils.loadProperties(encodedResource);

        mailSender.setHost(props.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(props.getProperty("spring.mail.port")));
        mailSender.setUsername(props.getProperty("spring.mail.username"));
        mailSender.setPassword(props.getProperty("spring.mail.password"));

        mailProperties.put("mail.smtp.auth", props.getProperty("spring.mail.properties.mail.smtp.auth"));
        mailProperties.put("mail.smtp.starttls.enable", props.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));

        mailSender.setJavaMailProperties(mailProperties);
        return mailSender;
    }
}
