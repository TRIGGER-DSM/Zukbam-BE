package com.example.zukbambe.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class VerifyEmailRequest {
    @Email(regexp = ".+@dsm\\.hs\\.kr$", message = "dsm.hs.kr 도메인의 이메일만 사용 가능합니다.")
    @NotBlank(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotBlank(message = "인증 id는 비어있을 수 없습니다.")
    private String verificationId;

    @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 6자리 숫자입니다.")
    @NotBlank(message = "인증 코드는 비어있을 수 없습니다.")
    private String verificationCode;
}
