package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemSummaryResponse(
    Long problemId,
    String title,
    ProblemRankResponse rank,
    List<CategoryResponse> categories
) {
    public static ProblemSummaryResponse of(Problem problem, List<CategoryResponse> categories) {
        return ProblemSummaryResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .rank(ProblemRankResponse.from(problem.getRank()))
            .categories(categories)
            .build();
    }
}
