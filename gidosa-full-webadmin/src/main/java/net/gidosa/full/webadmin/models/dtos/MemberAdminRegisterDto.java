package net.gidosa.full.webadmin.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberAdminRegisterDto {
    @NotBlank(message = "로그인 아이디는 필수 입력값입니다.")
//    @Size(min = 4, max = 20, message = "로그인 아이디은 4~20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "로그인 아이디는 영문과 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
//    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이여야 합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phone;

    private String location;

    private Long constructionId;

    @NotBlank(message = "권한은 필수 입력값입니다.")
    private String role;
} 