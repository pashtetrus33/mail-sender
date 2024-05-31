package project.mailer;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private String subject = "";
    private String text = "";

    public List<EmailDetails> parseExcelFile(File file) throws IOException {
        List<EmailDetails> emailDetailsList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int emailColumnIndex = -1;
            int subjectColumnIndex = -1;
            int textColumnIndex = -1;
            int attachmentColumnIndex = -1;

            if (rows.hasNext()) {
                Row headerRow = rows.next();
                Iterator<Cell> headerCells = headerRow.iterator();
                int index = 0;
                while (headerCells.hasNext()) {
                    Cell cell = headerCells.next();
                    switch (cell.getStringCellValue().toLowerCase()) {
                        case "email":
                            emailColumnIndex = index;
                            break;
                        case "subject":
                            subjectColumnIndex = index;
                            break;
                        case "text":
                            textColumnIndex = index;
                            break;
                        case "attachment":
                            attachmentColumnIndex = index;
                            break;
                    }
                    index++;
                }
            }

            if (emailColumnIndex == -1 || subjectColumnIndex == -1 || textColumnIndex == -1 || attachmentColumnIndex == -1) {
                throw new IllegalArgumentException("Required columns not found");
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Cell emailCell = currentRow.getCell(emailColumnIndex);
                Cell subjectCell = currentRow.getCell(subjectColumnIndex);
                Cell textCell = currentRow.getCell(textColumnIndex);
                Cell attachmentCell = currentRow.getCell(attachmentColumnIndex);


                    String attachmentPath = attachmentCell != null && attachmentCell.getCellType() == CellType.STRING ? attachmentCell.getStringCellValue() : null;

                    if (text.isEmpty()) {
                        text = textCell.getStringCellValue();
                    }
                    if (subject.isEmpty()) {
                        subject = subjectCell.getStringCellValue();
                    }

                    EmailDetails emailDetails = new EmailDetails(
                            emailCell.getStringCellValue(),
                            subject,
                            text,
                            attachmentPath
                    );
                    emailDetailsList.add(emailDetails);

            }
        }
        return emailDetailsList;
    }

    public static class EmailDetails {
        private String email;
        private String subject;
        private String text;
        private String attachmentPath;

        public EmailDetails(String email, String subject, String text, String attachmentPath) {
            this.email = email;
            this.subject = subject;
            this.text = text;
            this.attachmentPath = attachmentPath;
        }

        public String getEmail() {
            return email;
        }

        public String getSubject() {
            return subject;
        }

        public String getText() {
            return text;
        }

        public String getAttachmentPath() {
            return attachmentPath;
        }
    }
}