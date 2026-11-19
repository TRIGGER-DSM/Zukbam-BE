package com.example.zukbambe.domain.streak.presentation.dto.response;

import com.example.zukbambe.domain.streak.domain.Streak;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StreakResponse(
    int currentStreak,
    int maxStreak,
    LocalDate lastSolvedDate
) {
    public static StreakResponse of(Streak streak, LocalDate today) {
        return StreakResponse.builder()
            .currentStreak(streak.getCurrentStreak(today))
            .maxStreak(streak.getMaxStreak())
            .lastSolvedDate(streak.getLastSolvedDate())
            .build();
    }

    public static StreakResponse empty() {
        return StreakResponse.builder().build();
    }
}
