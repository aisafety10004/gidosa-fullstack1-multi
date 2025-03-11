package net.gidosa.full.webadmin.utils;

import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExportUtil {

    /**
     * Exports risk factors to Excel format
     * 
     * @param riskFactors List of risk factors to export
     * @return Byte array of Excel file
     * @throws IOException If an I/O error occurs
     */
    public static byte[] exportRiskFactorsToExcel(List<RiskFactor> riskFactors) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("위험요인 목록");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            String[] headers = {
                "번호", "현장명", "작업공정", "작업위치", "위험분류", "위험요인", "실시일자", "개선결과"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000); // Set column width
            }
            
            // Create data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            CellStyle dataCellStyle = createDataCellStyle(workbook);
            
            for (int i = 0; i < riskFactors.size(); i++) {
                RiskFactor riskFactor = riskFactors.get(i);
                Row row = sheet.createRow(i + 1);
                
                // 번호 (순번)
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(riskFactors.size() - i);
                cell0.setCellStyle(dataCellStyle);
                
                // 현장명
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(riskFactor.getSiteName());
                cell1.setCellStyle(dataCellStyle);
                
                // 작업공정
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(riskFactor.getWorkProcess());
                cell2.setCellStyle(dataCellStyle);
                
                // 작업위치
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(riskFactor.getWorkLocation() != null ? riskFactor.getWorkLocation() : "");
                cell3.setCellStyle(dataCellStyle);
                
                // 위험분류
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(riskFactor.getRiskClassification() != null ? 
                        riskFactor.getRiskClassification().getDisplayName() : "");
                cell4.setCellStyle(dataCellStyle);
                
                // 위험요인
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(riskFactor.getRiskDetailFactor() != null ? 
                        riskFactor.getRiskDetailFactorDisplayName() : "");
                cell5.setCellStyle(dataCellStyle);
                
                // 실시일자
                Cell cell6 = row.createCell(6);
                LocalDate executionDate = riskFactor.getExecutionDate();
                cell6.setCellValue(executionDate != null ? executionDate.format(dateFormatter) : "");
                cell6.setCellStyle(dataCellStyle);
                
                // 개선결과
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(riskFactor.getImpResult() != null ? riskFactor.getImpResult() : "");
                cell7.setCellStyle(dataCellStyle);
            }
            
            // Auto-size columns for better readability
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private static CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
} 