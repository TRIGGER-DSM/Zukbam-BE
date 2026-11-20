package com.example.zukbambe.global.judge.dto;

import com.example.zukbambe.global.judge.enums.JudgeStatus;

// 기대 출력이 없어서 정상 종료하면 ACCEPTED. time은 초, memory는 KB이고 측정되지 않으면 null
public record JudgeRunResult(
    JudgeStatus status,
    String stdout,
    String stderr,
    String compileOutput,
    Double time,
    Long memory
) {
}
