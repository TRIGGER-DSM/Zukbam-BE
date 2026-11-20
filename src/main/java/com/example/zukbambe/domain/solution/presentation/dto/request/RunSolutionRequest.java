package com.example.zukbambe.domain.solution.presentation.dto.request;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RunSolutionRequest(
    @NotNull(message = "언어는 필수입니다.")
    Language language,

    @NotBlank(message = "코드는 필수입니다.")
    @Size(max = 65536, message = "코드는 65536자 이하여야 합니다.")
    String code,

    @NotEmpty(message = "입력은 1개 이상이어야 합니다.")
    @Size(max = 5, message = "입력은 5개 이하여야 합니다.")
    List<
        @NotNull(message = "입력은 null일 수 없습니다.")
        @Size(max = 10000, message = "입력은 10000자 이하여야 합니다.")
        String
    > inputs
) {
}
