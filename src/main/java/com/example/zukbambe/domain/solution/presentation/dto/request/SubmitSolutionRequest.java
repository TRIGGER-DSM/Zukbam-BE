package com.example.zukbambe.domain.solution.presentation.dto.request;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitSolutionRequest(
    @NotNull(message = "언어는 필수입니다.")
    Language language,

    @NotBlank(message = "코드는 필수입니다.")
    @Size(max = 65536, message = "코드는 65536자 이하여야 합니다.")
    String code
) {
}
