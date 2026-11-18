package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemDetailResponse(
    Long problemId,
    String title,
    String content,
    Long memoryLimit,
    Double timeLimit,
    ProblemRankResponse rank,
    List<CategoryResponse> categories,
    List<TestCaseResponse> testCases
) {
    public static ProblemDetailResponse of(Problem problem, List<CategoryResponse> categories, List<TestCase> testCases) {
        return ProblemDetailResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .content(problem.getContent())
            .memoryLimit(problem.getMemoryLimit())
            .timeLimit(problem.getTimeLimit())
            .rank(ProblemRankResponse.from(problem.getRank()))
            .categories(categories)
            .testCases(testCases.stream().map(TestCaseResponse::from).toList())
            .build();
    }
}
