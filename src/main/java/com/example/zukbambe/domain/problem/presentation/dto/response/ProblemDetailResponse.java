package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemRank;
import com.example.zukbambe.domain.testcase.domain.TestCase;

import java.util.List;

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
        return new ProblemDetailResponse(
            problem.getProblemId(),
            problem.getTitle(),
            problem.getContent(),
            problem.getMemoryLimit(),
            problem.getTimeLimit(),
            problem.getRank(),
            testCases.stream().map(TestCaseResponse::from).toList()
        );
    }
}
