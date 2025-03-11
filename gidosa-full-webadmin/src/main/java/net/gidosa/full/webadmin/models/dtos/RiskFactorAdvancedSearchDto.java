package net.gidosa.full.webadmin.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for advanced search functionality in list-pc.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorAdvancedSearchDto {
    
    private String siteName;             // 현장명
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateStart;  // 실시일자 시작
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateEnd;    // 실시일자 종료
    
    private String workProcess;          // 작업공정
    
    private String workLocation;         // 작업위치
    
    private RiskFactor.RiskClassification riskClassification;  // 위험분류
    
    private String riskDetailFactor;     // 위험요인
    
    private String impResult;            // 개선결과
} 