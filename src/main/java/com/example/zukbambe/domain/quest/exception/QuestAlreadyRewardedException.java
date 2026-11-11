package com.example.zukbambe.domain.quest.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class QuestAlreadyRewardedException extends ZukBamException {
    public QuestAlreadyRewardedException() {
        super(QuestErrorCode.QUEST_ALREADY_REWARDED);
    }
}
