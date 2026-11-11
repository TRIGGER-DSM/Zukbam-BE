package com.example.zukbambe.domain.quest.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class QuestNotCompletedException extends ZukBamException {
    public QuestNotCompletedException() {
        super(QuestErrorCode.QUEST_NOT_COMPLETED);
    }
}
