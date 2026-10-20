package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SolutionErrorCode implements ErrorProperty {
    SOLUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SO001", "풀이를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
