package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.testcase.domain.TestCase;

public record TestCaseResponse(
    Long testcaseId,
    String input,
    String output,
    Boolean isSample
) {
    public static TestCaseResponse from(TestCase testCase) {
        return new TestCaseResponse(
            testCase.getTestcaseId(),
            testCase.getExampleRead(),
            testCase.getExampleWrite(),
            testCase.getIsSample()
        );
    }
}
