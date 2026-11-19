package com.example.zukbambe.domain.ranking.presentation.dto.response;

import com.example.zukbambe.domain.ranking.domain.repository.RankingRow;
import lombok.Builder;

@Builder
public record RankingResponse(
    Long position,
    Long userId,
    String name,
    String rankName,
    Long xp,
    Long solvedCount,
    Integer streak
) {
    public static RankingResponse from(RankingRow row) {
        return RankingResponse.builder()
            .position(row.getPosition())
            .userId(row.getUserId())
            .name(row.getName())
            .rankName(row.getRankName())
            .xp(row.getXp())
            .solvedCount(row.getSolvedCount())
            .streak(row.getStreak())
            .build();
    }
}
