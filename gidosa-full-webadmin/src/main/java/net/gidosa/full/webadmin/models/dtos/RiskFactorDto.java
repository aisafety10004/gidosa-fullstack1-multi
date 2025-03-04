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
public class RiskFactorDto {
    
    private Long id;
    
    private String name;
    
    private Long constructionId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String description;
    
    private RiskFactor.RiskStatus status;
    
    private String locationDetail;
    
    private RiskFactor.RiskLevel riskLevel;
    
    // Convert DTO to Entity
    public RiskFactor toEntity() {
        return RiskFactor.builder()
                .id(id)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .status(status)
                .locationDetail(locationDetail)
                .riskLevel(riskLevel)
                .build();
    }
    
    // Convert Entity to DTO
    public static RiskFactorDto fromEntity(RiskFactor riskFactor) {
        return RiskFactorDto.builder()
                .id(riskFactor.getId())
                .name(riskFactor.getName())
                .constructionId(riskFactor.getConstruction().getId())
                .startDate(riskFactor.getStartDate())
                .endDate(riskFactor.getEndDate())
                .description(riskFactor.getDescription())
                .status(riskFactor.getStatus())
                .locationDetail(riskFactor.getLocationDetail())
                .riskLevel(riskFactor.getRiskLevel())
                .build();
    }
} 