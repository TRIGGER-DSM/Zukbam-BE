package com.example.zukbambe.domain.solution.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    C99("C99"),
    CPP17("C++17"),
    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    TEXT("Text");

    @JsonValue
    private final String displayName;
}
