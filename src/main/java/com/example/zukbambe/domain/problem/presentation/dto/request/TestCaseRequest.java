package com.example.zukbambe.domain.problem.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record TestCaseRequest(
    String input,

    @NotNull(message = "출력값은 필수입니다.")
    String output,

    @NotNull(message = "샘플 여부는 필수입니다.")
    Boolean isSample
) {
}
