package com.example.zukbambe.domain.rank.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RankErrorCode implements ErrorProperty {
    USER_RANK_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "R001", "기본 랭크가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
