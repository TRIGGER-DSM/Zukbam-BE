package com.example.zukbambe.domain.rank.presentation.dto.response;

import com.example.zukbambe.domain.rank.domain.ProblemRank;

import java.util.List;

public record ProblemRankListResponse(
    List<ProblemRankResponse> problemRanks
) {
    public static ProblemRankListResponse from(List<ProblemRank> ranks) {
        return new ProblemRankListResponse(ranks.stream().map(ProblemRankResponse::from).toList());
    }
}
