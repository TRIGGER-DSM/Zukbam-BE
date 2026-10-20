package com.example.zukbambe.domain.solution.domain.enums;

import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    C99("C99", JudgeLanguage.C99),
    CPP17("C++17", JudgeLanguage.CPP17),
    JAVA("Java", JudgeLanguage.JAVA),
    PYTHON("Python", JudgeLanguage.PYTHON),
    JAVASCRIPT("JavaScript", JudgeLanguage.JAVASCRIPT),
    TEXT("Text", JudgeLanguage.TEXT);

    @JsonValue
    private final String displayName;
    private final JudgeLanguage judgeLanguage;
}
