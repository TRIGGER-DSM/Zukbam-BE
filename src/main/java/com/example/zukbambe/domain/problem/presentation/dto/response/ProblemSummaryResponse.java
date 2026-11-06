package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
import lombok.Builder;

@Builder
public record ProblemSummaryResponse(
    Long problemId,
    String title,
    ProblemRankResponse rank
) {
    public static ProblemSummaryResponse from(Problem problem) {
        return ProblemSummaryResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .rank(ProblemRankResponse.from(problem.getRank()))
            .build();
    }
}
