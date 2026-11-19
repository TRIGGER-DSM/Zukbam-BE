package com.example.zukbambe.domain.streak.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StreakErrorCode implements ErrorProperty {
    INVALID_GRASS_PERIOD(HttpStatus.BAD_REQUEST, "ST001", "잔디 조회 기간이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
