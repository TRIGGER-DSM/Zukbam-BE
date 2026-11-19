package com.example.zukbambe.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignInRequest {
    @Email(regexp = ".+@dsm\\.hs\\.kr$", message = "dsm.hs.kr 도메인의 이메일만 사용 가능합니다.")
    @NotBlank(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;
}
