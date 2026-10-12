package com.example.zukbambe.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TeacherSignUpRequest {
    @Email(regexp = ".+@dsm\\.hs\\.kr$", message = "dsm.hs.kr 도메인의 이메일만 사용 가능합니다.")
    @NotBlank(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @Size(max = 255, message = "이름은 255자 이하여야 합니다.")
    @NotBlank(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,64}$",
        message = "비밀번호는 8~64자이며 숫자, 영문, 특수문자를 포함해야 합니다."
    )
    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;

    @NotBlank(message = "teacherCode는 비어있을 수 없습니다.")
    private String teacherCode;

    @NotBlank(message = "이메일 인증 토큰은 비어있을 수 없습니다.")
    private String emailVerifiedToken;
}
