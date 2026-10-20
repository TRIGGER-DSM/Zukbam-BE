package com.example.zukbambe.global.judge.dto;

import com.example.zukbambe.global.judge.enums.JudgeLanguage;

import java.util.List;

// timeLimit은 초, memoryLimit은 KB
public record JudgeRequest(
    JudgeLanguage language,
    String code,
    List<JudgeCase> cases,
    Double timeLimit,
    Long memoryLimit
) {
}
