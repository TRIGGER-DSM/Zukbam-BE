package com.example.zukbambe.domain.rank.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateProblemRankRequest(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 255, message = "이름은 255자 이하여야 합니다.")
    String name,

    @NotNull(message = "XP는 필수입니다.")
    @PositiveOrZero(message = "XP는 0 이상이어야 합니다.")
    Long xp
) {
}
