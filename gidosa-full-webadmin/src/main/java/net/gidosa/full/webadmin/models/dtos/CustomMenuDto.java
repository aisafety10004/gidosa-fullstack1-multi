package net.gidosa.full.webadmin.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMenuDto {
    private Long id;
    
    @NotBlank(message = "메뉴 이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "메뉴 URL은 필수입니다")
    private String url;
    
    private String description;
    
    @NotNull(message = "메뉴 타입은 필수입니다")
    private Integer menuType; // 1: 단건 내용 저장/보기, 2: 날짜 저장/보기
    
    private Long parentId; // 상위 메뉴 ID (null이면 최상위 메뉴)
    
    private Integer displayOrder; // 표시 순서
    
    private Boolean isActive; // 활성화 여부
    
    private Long constructionId; // 건설 현장 ID
} 