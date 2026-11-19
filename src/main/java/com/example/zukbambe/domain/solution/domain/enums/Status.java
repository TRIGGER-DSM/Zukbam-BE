package com.example.zukbambe.domain.solution.domain.enums;

import com.example.zukbambe.global.judge.enums.JudgeStatus;

public enum Status {
    PENDING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILE_ERROR,
    INTERNAL_ERROR;

    public static Status from(JudgeStatus judgeStatus) {
        return switch (judgeStatus) {
            case ACCEPTED -> ACCEPTED;
            case WRONG_ANSWER -> WRONG_ANSWER;
            case TIME_LIMIT_EXCEEDED -> TIME_LIMIT_EXCEEDED;
            case RUNTIME_ERROR -> RUNTIME_ERROR;
            case COMPILE_ERROR -> COMPILE_ERROR;
            case INTERNAL_ERROR -> INTERNAL_ERROR;
        };
    }
}
