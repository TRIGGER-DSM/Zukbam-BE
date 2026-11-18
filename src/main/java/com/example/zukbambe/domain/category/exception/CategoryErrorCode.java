package com.example.zukbambe.domain.category.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements ErrorProperty {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "분류를 찾을 수 없습니다."),
    CATEGORY_NAME_DUPLICATED(HttpStatus.CONFLICT, "CA002", "이미 존재하는 분류 이름입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
