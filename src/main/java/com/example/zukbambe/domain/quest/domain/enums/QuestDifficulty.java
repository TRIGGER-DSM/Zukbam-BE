package com.example.zukbambe.domain.quest.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestDifficulty {
    EASY(10),
    NORMAL(20),
    HARD(40);

    private final long rewardXp;
}
