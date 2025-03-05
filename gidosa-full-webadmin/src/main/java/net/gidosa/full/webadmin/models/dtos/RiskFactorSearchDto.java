package net.gidosa.full.webadmin.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorSearchDto {
    
    private String siteName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateStart;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDateEnd;
} 