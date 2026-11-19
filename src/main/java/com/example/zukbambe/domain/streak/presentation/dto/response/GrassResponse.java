package com.example.zukbambe.domain.streak.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
public record GrassResponse(
    LocalDate from,
    LocalDate to,
    List<GrassDayResponse> days
) {
    public static GrassResponse of(LocalDate from, LocalDate to, Map<LocalDate, Long> solvedCounts) {
        return GrassResponse.builder()
            .from(from)
            .to(to)
            .days(solvedCounts.entrySet().stream()
                .map(entry -> new GrassDayResponse(entry.getKey(), entry.getValue()))
                .toList())
            .build();
    }
}
