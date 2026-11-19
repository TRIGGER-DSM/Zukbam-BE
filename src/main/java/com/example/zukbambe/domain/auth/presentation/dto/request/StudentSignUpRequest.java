package com.example.zukbambe.domain.auth.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class StudentSignUpRequest {
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

    @Min(value = 1, message = "학년은 1~3 사이여야 합니다.")
    @Max(value = 3, message = "학년은 1~3 사이여야 합니다.")
    @NotNull(message = "학년은 비어있을 수 없습니다.")
    private Short grade;

    @JsonProperty("class")
    @Min(value = 1, message = "반은 1~4 사이여야 합니다.")
    @Max(value = 4, message = "반은 1~4 사이여야 합니다.")
    @NotNull(message = "반은 비어있을 수 없습니다.")
    private Short classNumber;

    @Positive(message = "번호는 양수여야 합니다.")
    @NotNull(message = "번호는 비어있을 수 없습니다.")
    private Short number;

    @NotBlank(message = "이메일 인증 토큰은 비어있을 수 없습니다.")
    private String emailVerifiedToken;
}
