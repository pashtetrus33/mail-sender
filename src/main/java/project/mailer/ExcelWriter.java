package project.mailer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ExcelWriter {

    public void writeAttachmentsNamesToExcel(String filename) {
        // Получаем список файлов из папки и сортируем их
        File folder = new File("attachments");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".pdf"));
        if (listOfFiles == null) {
            System.out.println("Папка не найдена или не содержит файлов PDF.");
            return;
        }

        List<String> fileNames = Arrays.stream(listOfFiles)
                .map(File::getName)
                .sorted(String::compareToIgnoreCase)
                .toList();

        try (FileInputStream inputStream = new FileInputStream(filename);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Ищем заголовок "attachment" в первой строке (нулевой строке)
            Row headerRow = sheet.getRow(0);
            int attachmentColumnIndex = -1;
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().equalsIgnoreCase("attachment")) {
                    attachmentColumnIndex = cell.getColumnIndex();
                    break;
                }
            }

            // Если столбец с именем "attachment" найден, заполняем его
            if (attachmentColumnIndex != -1) {
                int rowNum = 1; // Начинаем со второй строки (индекс 1)

                if (fileNames.size() == 1) {
                    // Если файл один, записываем его во все строки столбца 'attachment'
                    String fileName = fileNames.get(0);
                    while (true) {
                        Row row = sheet.getRow(rowNum);
                        if (row == null) {
                            break;
                        }
                        Cell cell = row.createCell(attachmentColumnIndex);
                        cell.setCellValue(fileName);
                        rowNum++;
                    }
                } else {
                    // Если файлов несколько, записываем их по одному в каждую строку
                    for (String fileName : fileNames) {
                        Row row = sheet.getRow(rowNum++);
                        if (row == null) {
                            row = sheet.createRow(rowNum - 1);
                        }
                        Cell cell = row.createCell(attachmentColumnIndex);
                        cell.setCellValue(fileName);
                    }
                }
            } else {
                System.out.println("Столбец 'attachment' не найден.");
            }

            // Записываем изменения обратно в файл Excel
            try (FileOutputStream outputStream = new FileOutputStream(filename)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExcelWriter writer = new ExcelWriter();
        writer.writeAttachmentsNamesToExcel("example.xlsx");
    }
}
