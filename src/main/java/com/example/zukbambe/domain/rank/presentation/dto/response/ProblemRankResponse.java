package com.example.zukbambe.domain.rank.presentation.dto.response;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import lombok.Builder;

@Builder
public record ProblemRankResponse(
    Long problemRankId,
    String name,
    Long xp,
    Integer sortOrder
) {
    public static ProblemRankResponse from(ProblemRank rank) {
        return ProblemRankResponse.builder()
            .problemRankId(rank.getProblemRankId())
            .name(rank.getName())
            .xp(rank.getXp())
            .sortOrder(rank.getSortOrder())
            .build();
    }
}
