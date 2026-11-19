package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemSolveStatus;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemSummaryResponse(
    Long problemId,
    String title,
    ProblemRankResponse rank,
    List<CategoryResponse> categories,
    long solverCount,
    Double averageAttempts,
    ProblemSolveStatus status
) {
    // averageAttempts는 푼 사람이 없으면 null이고, status는 로그인하지 않았으면 null이다
    public static ProblemSummaryResponse of(
        Problem problem,
        List<CategoryResponse> categories,
        long solverCount,
        Double averageAttempts,
        ProblemSolveStatus status
    ) {
        return ProblemSummaryResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .rank(ProblemRankResponse.from(problem.getRank()))
            .categories(categories)
            .solverCount(solverCount)
            .averageAttempts(averageAttempts)
            .status(status)
            .build();
    }
}
