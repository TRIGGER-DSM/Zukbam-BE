package com.example.zukbambe.domain.category.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "분류 이름은 필수입니다.")
    @Size(max = 255, message = "분류 이름은 255자 이하여야 합니다.")
    String name
) {
}
