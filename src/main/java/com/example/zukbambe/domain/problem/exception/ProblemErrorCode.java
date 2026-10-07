package com.example.zukbambe.domain.problem.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProblemErrorCode implements ErrorProperty {
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "문제를 찾을 수 없습니다."),
    PROBLEM_TITLE_DUPLICATED(HttpStatus.CONFLICT, "P002", "이미 존재하는 문제 제목입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
