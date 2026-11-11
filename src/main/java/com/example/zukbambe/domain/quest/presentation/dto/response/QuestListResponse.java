package com.example.zukbambe.domain.quest.presentation.dto.response;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record QuestListResponse(
    List<QuestResponse> quests,
    LocalDateTime resetAt
) {
    public static QuestListResponse of(
        List<DailyQuest> quests,
        DailyActivity activity,
        List<ProblemRank> ranks,
        List<DailyQuest> rewardedQuests,
        LocalDateTime resetAt
    ) {
        return QuestListResponse.builder()
            .quests(quests.stream()
                .map(quest -> QuestResponse.of(quest, activity, ranks, rewardedQuests.contains(quest)))
                .toList())
            .resetAt(resetAt)
            .build();
    }
}
