package com.example.zukbambe.domain.quest.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestErrorCode implements ErrorProperty {
    NOT_TODAY_QUEST(HttpStatus.NOT_FOUND, "Q001", "오늘의 퀘스트가 아닙니다."),
    QUEST_NOT_COMPLETED(HttpStatus.CONFLICT, "Q002", "아직 완료하지 않은 퀘스트입니다."),
    QUEST_ALREADY_REWARDED(HttpStatus.CONFLICT, "Q003", "이미 보상을 받은 퀘스트입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
