package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemRank;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemDetailResponse(
    Long problemId,
    String title,
    String content,
    Long memoryLimit,
    Long timeLimit,
    ProblemRank rank,
    List<TestCaseResponse> testCases
) {
    public static ProblemDetailResponse of(Problem problem, List<TestCase> testCases) {
        return ProblemDetailResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .content(problem.getContent())
            .memoryLimit(problem.getMemoryLimit())
            .timeLimit(problem.getTimeLimit())
            .rank(problem.getRank())
            .testCases(testCases.stream().map(TestCaseResponse::from).toList())
            .build();
    }
}
