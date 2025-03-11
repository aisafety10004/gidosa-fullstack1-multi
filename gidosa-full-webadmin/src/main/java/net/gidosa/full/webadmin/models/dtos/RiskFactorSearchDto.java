package net.gidosa.full.webadmin.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for basic search functionality in list.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorSearchDto {
    
    private String siteName;             // 현장명
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateStart;  // 등록일자(실시일자) 시작
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateEnd;    // 등록일자(실시일자) 종료
} 