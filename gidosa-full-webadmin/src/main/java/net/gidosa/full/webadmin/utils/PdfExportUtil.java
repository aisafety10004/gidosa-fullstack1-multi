package net.gidosa.full.webadmin.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExportUtil {

    /**
     * Exports risk factors to PDF format
     * 
     * @param riskFactors List of risk factors to export
     * @return Byte array of PDF file
     * @throws IOException If an I/O error occurs
     * @throws DocumentException If a document error occurs
     */
    public static byte[] exportRiskFactorsToPdf(List<RiskFactor> riskFactors) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Create document with A4 size
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);
        
        // Open document
        document.open();
        
        // Create Korean font using iText Asian fonts
        BaseFont baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font koreanTitleFont = new Font(baseFont, 16, Font.BOLD);
        Font koreanHeaderFont = new Font(baseFont, 10, Font.BOLD);
        Font koreanDataFont = new Font(baseFont, 9, Font.NORMAL);
        
        // Add title
        Paragraph title = new Paragraph("위험요인 목록", koreanTitleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Create table
        PdfPTable table = new PdfPTable(8); // 8 columns
        table.setWidthPercentage(100);
        
        // Set column widths
        float[] columnWidths = {0.5f, 1.5f, 1.5f, 1.5f, 1.2f, 2.0f, 1.0f, 1.8f};
        table.setWidths(columnWidths);
        
        // Add table headers
        String[] headers = {
            "번호", "현장명", "작업공정", "작업위치", "위험분류", "위험요인", "실시일자", "개선결과"
        };
        
        BaseColor headerBgColor = new BaseColor(220, 220, 220);
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, koreanHeaderFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(headerBgColor);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Add data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 0; i < riskFactors.size(); i++) {
            RiskFactor riskFactor = riskFactors.get(i);
            
            // 번호 (순번)
            PdfPCell cell0 = new PdfPCell(new Phrase(String.valueOf(riskFactors.size() - i), koreanDataFont));
            cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell0.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell0.setPadding(5);
            table.addCell(cell0);
            
            // 현장명
            PdfPCell cell1 = new PdfPCell(new Phrase(riskFactor.getSiteName(), koreanDataFont));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell1.setPadding(5);
            table.addCell(cell1);
            
            // 작업공정
            PdfPCell cell2 = new PdfPCell(new Phrase(riskFactor.getWorkProcess(), koreanDataFont));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setPadding(5);
            table.addCell(cell2);
            
            // 작업위치
            PdfPCell cell3 = new PdfPCell(new Phrase(
                    riskFactor.getWorkLocation() != null ? riskFactor.getWorkLocation() : "", koreanDataFont));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setPadding(5);
            table.addCell(cell3);
            
            // 위험분류
            PdfPCell cell4 = new PdfPCell(new Phrase(
                    riskFactor.getRiskClassification() != null ? 
                    riskFactor.getRiskClassification().getDisplayName() : "", koreanDataFont));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell4.setPadding(5);
            table.addCell(cell4);
            
            // 위험요인
            PdfPCell cell5 = new PdfPCell(new Phrase(
                    riskFactor.getRiskDetailFactor() != null ? 
                    riskFactor.getRiskDetailFactorDisplayName() : "", koreanDataFont));
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell5.setPadding(5);
            table.addCell(cell5);
            
            // 실시일자
            PdfPCell cell6 = new PdfPCell(new Phrase(
                    riskFactor.getExecutionDate() != null ? 
                    riskFactor.getExecutionDate().format(dateFormatter) : "", koreanDataFont));
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell6.setPadding(5);
            table.addCell(cell6);
            
            // 개선결과
            PdfPCell cell7 = new PdfPCell(new Phrase(
                    riskFactor.getImpResult() != null ? riskFactor.getImpResult() : "", koreanDataFont));
            cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell7.setPadding(5);
            table.addCell(cell7);
        }
        
        document.add(table);
        document.close();
        
        return outputStream.toByteArray();
    }
} 