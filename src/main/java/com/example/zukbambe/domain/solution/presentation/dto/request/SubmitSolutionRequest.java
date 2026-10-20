package com.example.zukbambe.domain.solution.presentation.dto.request;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitSolutionRequest(
    @NotNull(message = "언어는 필수입니다.")
    Language language,

    @NotBlank(message = "코드는 필수입니다.")
    String code
) {
}
