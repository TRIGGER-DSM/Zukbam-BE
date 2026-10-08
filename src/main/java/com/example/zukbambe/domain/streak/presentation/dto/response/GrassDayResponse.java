package com.example.zukbambe.domain.streak.presentation.dto.response;

import java.time.LocalDate;

public record GrassDayResponse(
    LocalDate date,
    long solvedCount
) {
}
