package net.gidosa.full.webapp.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class MemberRegisterDto {
    private Long constructionId;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문과 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "휴대폰번호는 필수 입력값입니다.")
    private String phone;
    
    private String position; // 직책
    
    private String jobType; // 직종
    
    private String emergencyContact; // 비상연락번호
    
    private MultipartFile profilePhotoFile; // 프로필 사진
    
    private MultipartFile laborContractFile; // 근로계약서
    
    private MultipartFile safetyEducationCertFile; // 건설업기초안전보건교육이수증
    
    private MultipartFile protectiveGearPledgeFile; // 보호구착용서약서
    
    private MultipartFile etcDoc1File; // 기타문서1
    private String etcDoc1Description; // 기타문서1 설명
    
    private MultipartFile etcDoc2File; // 기타문서2
    private String etcDoc2Description; // 기타문서2 설명
    
    private MultipartFile etcDoc3File; // 기타문서3
    private String etcDoc3Description; // 기타문서3 설명
} 