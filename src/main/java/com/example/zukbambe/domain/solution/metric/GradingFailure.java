package com.example.zukbambe.domain.solution.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 사용자 코드 문제(오답·시간초과 등)가 아니라 채점 시스템 쪽 원인으로 INTERNAL_ERROR가 된 이유
@Getter
@AllArgsConstructor
public enum GradingFailure {
    JUDGE_ERROR("judge_error"),
    JUDGE_REPORTED("judge_reported"),
    NO_TESTCASE("no_testcase"),
    QUEUE_FULL("queue_full");

    private final String key;
}
