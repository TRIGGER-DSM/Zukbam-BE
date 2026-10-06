package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemRank;

public record ProblemSummaryResponse(
    Long problemId,
    String title,
    ProblemRank rank
) {
    public static ProblemSummaryResponse from(Problem problem) {
        return new ProblemSummaryResponse(
            problem.getProblemId(),
            problem.getTitle(),
            problem.getRank()
        );
    }
}
