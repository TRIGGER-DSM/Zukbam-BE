package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorProperty {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "아이디 혹은 비밀번호가 맞지 않습니다."),
    VERIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "A002", "인증 정보가 만료되었거나 존재하지 않습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "A003", "인증 코드가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
