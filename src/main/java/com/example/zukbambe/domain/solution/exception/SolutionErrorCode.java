package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SolutionErrorCode implements ErrorProperty {
    SOLUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SO001", "풀이를 찾을 수 없습니다."),
    TOO_MANY_SUBMISSIONS(HttpStatus.TOO_MANY_REQUESTS, "SO002", "제출이 너무 많습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
