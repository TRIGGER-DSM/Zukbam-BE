package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.testcase.domain.TestCase;
import lombok.Builder;

@Builder
public record TestCaseResponse(
    Long testcaseId,
    String input,
    String output,
    Boolean isSample
) {
    public static TestCaseResponse from(TestCase testCase) {
        return TestCaseResponse.builder()
            .testcaseId(testCase.getTestcaseId())
            .input(testCase.getExampleRead())
            .output(testCase.getExampleWrite())
            .isSample(testCase.getIsSample())
            .build();
    }
}
