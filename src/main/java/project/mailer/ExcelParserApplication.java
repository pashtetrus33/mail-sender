package project.mailer;

import jakarta.mail.MessagingException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class ExcelParserApplication implements CommandLineRunner {

    private final ExcelService excelService;
    private final EmailService emailService;

    public ExcelParserApplication(ExcelService excelService, EmailService emailService) {
        this.excelService = excelService;
        this.emailService = emailService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ExcelParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        File file = new File("data_for_sending.xlsx");

        if (file.exists() && !file.isDirectory()) {
            try {
                List<ExcelService.EmailDetails> emailDetailsList = excelService.parseExcelFile(file);
                for (ExcelService.EmailDetails emailDetails : emailDetailsList) {
                    try {
                        emailService.sendEmail(emailDetails.getEmail(), emailDetails.getSubject(), emailDetails.getText(), emailDetails.getAttachmentPath());
                        System.out.println("Email sent to: " + emailDetails.getEmail());
                    } catch (MessagingException e) {
                        System.err.println("Error sending email to " + emailDetails.getEmail() + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading the Excel file: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid file path provided: " + args[0]);
        }
    }
}