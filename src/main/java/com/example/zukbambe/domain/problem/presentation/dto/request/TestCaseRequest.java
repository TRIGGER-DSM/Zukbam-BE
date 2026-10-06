package com.example.zukbambe.domain.problem.presentation.dto.request;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import jakarta.validation.constraints.NotNull;

public record TestCaseRequest(
    String input,

    @NotNull(message = "출력값은 필수입니다.")
    String output,

    @NotNull(message = "샘플 여부는 필수입니다.")
    Boolean isSample
) {
    public TestCase toEntity(Problem problem) {
        return TestCase.builder()
            .problem(problem)
            .exampleRead(input)
            .exampleWrite(output)
            .isSample(isSample)
            .build();
    }
}
