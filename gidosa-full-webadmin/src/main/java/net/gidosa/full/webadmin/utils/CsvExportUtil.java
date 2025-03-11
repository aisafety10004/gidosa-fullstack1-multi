package net.gidosa.full.webadmin.utils;

import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExportUtil {

    /**
     * Exports risk factors to CSV format
     * 
     * @param riskFactors List of risk factors to export
     * @return Byte array of CSV file
     * @throws IOException If an I/O error occurs
     */
    public static byte[] exportRiskFactorsToCsv(List<RiskFactor> riskFactors) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        
        // Add BOM (Byte Order Mark) for Excel to recognize UTF-8
        outputStream.write(0xEF);
        outputStream.write(0xBB);
        outputStream.write(0xBF);
        
        // Write header row
        String[] headers = {
            "번호", "현장명", "작업공정", "작업위치", "위험분류", "위험요인", "실시일자", "개선결과"
        };
        
        writer.write(String.join(",", headers) + "\n");
        
        // Write data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 0; i < riskFactors.size(); i++) {
            RiskFactor riskFactor = riskFactors.get(i);
            
            String[] rowData = {
                String.valueOf(riskFactors.size() - i),
                escapeCsvField(riskFactor.getSiteName()),
                escapeCsvField(riskFactor.getWorkProcess()),
                escapeCsvField(riskFactor.getWorkLocation() != null ? riskFactor.getWorkLocation() : ""),
                escapeCsvField(riskFactor.getRiskClassification() != null ? 
                        riskFactor.getRiskClassification().getDisplayName() : ""),
                escapeCsvField(riskFactor.getRiskDetailFactor() != null ? 
                        riskFactor.getRiskDetailFactorDisplayName() : ""),
                riskFactor.getExecutionDate() != null ? 
                        riskFactor.getExecutionDate().format(dateFormatter) : "",
                escapeCsvField(riskFactor.getImpResult() != null ? riskFactor.getImpResult() : "")
            };
            
            writer.write(String.join(",", rowData) + "\n");
        }
        
        writer.flush();
        return outputStream.toByteArray();
    }
    
    /**
     * Escapes special characters in CSV fields
     * 
     * @param field The field to escape
     * @return Escaped field
     */
    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If the field contains commas, quotes, or newlines, wrap it in quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Replace any quotes with double quotes (CSV escaping)
            field = field.replace("\"", "\"\"");
            // Wrap in quotes
            return "\"" + field + "\"";
        }
        
        return field;
    }
} 