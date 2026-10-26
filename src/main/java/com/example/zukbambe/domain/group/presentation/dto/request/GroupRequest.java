package com.example.zukbambe.domain.group.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupRequest(
    @NotBlank(message = "그룹 이름은 필수입니다.")
    @Size(max = 255, message = "그룹 이름은 255자 이하여야 합니다.")
    String title,

    @Size(max = 255, message = "그룹 소개는 255자 이하여야 합니다.")
    String introduce
) {
}
