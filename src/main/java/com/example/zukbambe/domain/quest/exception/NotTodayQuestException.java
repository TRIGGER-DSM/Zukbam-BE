package com.example.zukbambe.domain.quest.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class NotTodayQuestException extends ZukBamException {
    public NotTodayQuestException() {
        super(QuestErrorCode.NOT_TODAY_QUEST);
    }
}
