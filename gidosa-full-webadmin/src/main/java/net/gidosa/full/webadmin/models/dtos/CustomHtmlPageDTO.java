package net.gidosa.full.webadmin.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTML 페이지 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomHtmlPageDTO {
    
    private Long id;
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    
    private boolean published;
    
    // 소속 공사 ID
    private Long constructionId;
    
    // 소속 공사 이름 (표시용)
    private String constructionName;
    
    // HTML 파일 관련 필드는 MultipartFile로 컨트롤러에서 별도 처리
} 