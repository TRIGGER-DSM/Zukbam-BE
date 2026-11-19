package com.example.zukbambe.domain.quest.presentation.dto.response;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.quest.domain.enums.QuestDifficulty;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import lombok.Builder;

import java.util.List;

@Builder
public record QuestResponse(
    DailyQuest type,
    QuestDifficulty difficulty,
    String title,
    int current,
    int target,
    boolean completed,
    long rewardXp,
    boolean rewarded
) {
    public static QuestResponse of(DailyQuest quest, DailyActivity activity, List<ProblemRank> ranks, boolean rewarded) {
        int current = quest.getCurrent(activity, ranks);
        return QuestResponse.builder()
            .type(quest)
            .difficulty(quest.getDifficulty())
            .title(quest.getTitle(ranks))
            .current(current)
            .target(quest.getTarget())
            .completed(current >= quest.getTarget())
            .rewardXp(quest.getDifficulty().getRewardXp())
            .rewarded(rewarded)
            .build();
    }
}
