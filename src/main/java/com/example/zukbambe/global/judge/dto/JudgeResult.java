package com.example.zukbambe.global.judge.dto;

import com.example.zukbambe.global.judge.enums.JudgeStatus;

// time은 초, memory는 KB. 측정되지 않으면 null
public record JudgeResult(
    JudgeStatus status,
    Double time,
    Long memory
) {
    public static JudgeResult internalError() {
        return new JudgeResult(JudgeStatus.INTERNAL_ERROR, null, null);
    }
}
