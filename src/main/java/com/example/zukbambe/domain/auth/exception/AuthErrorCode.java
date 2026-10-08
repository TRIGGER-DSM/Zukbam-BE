package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorProperty {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "아이디 혹은 비밀번호가 맞지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
