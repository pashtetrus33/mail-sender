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

        String[] fileNames = Arrays.stream(listOfFiles)
                .map(File::getName)
                .sorted(String::compareToIgnoreCase)
                .toArray(String[]::new);

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
                for (String fileName : fileNames) {
                    Row row = sheet.getRow(rowNum++);
                    if (row == null) {
                        row = sheet.createRow(rowNum);
                    }
                    Cell cell = row.createCell(attachmentColumnIndex);
                    cell.setCellValue(fileName);
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
}
