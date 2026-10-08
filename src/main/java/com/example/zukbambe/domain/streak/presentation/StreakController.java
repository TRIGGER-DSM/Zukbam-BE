package com.example.zukbambe.domain.streak.presentation;

import com.example.zukbambe.domain.streak.presentation.dto.response.GrassResponse;
import com.example.zukbambe.domain.streak.presentation.dto.response.StreakResponse;
import com.example.zukbambe.domain.streak.service.QueryMyGrassService;
import com.example.zukbambe.domain.streak.service.QueryMyStreakService;
import com.example.zukbambe.domain.streak.service.QueryUserGrassService;
import com.example.zukbambe.domain.streak.service.QueryUserStreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/streaks")
@RequiredArgsConstructor
public class StreakController {

    private final QueryMyStreakService queryMyStreakService;
    private final QueryMyGrassService queryMyGrassService;
    private final QueryUserStreakService queryUserStreakService;
    private final QueryUserGrassService queryUserGrassService;

    @GetMapping("/me")
    public StreakResponse getMyStreak() {
        return queryMyStreakService.execute();
    }

    @GetMapping("/me/grass")
    public GrassResponse getMyGrass(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryMyGrassService.execute(from, to);
    }

    @GetMapping("/{userId}")
    public StreakResponse getUserStreak(@PathVariable Long userId) {
        return queryUserStreakService.execute(userId);
    }

    @GetMapping("/{userId}/grass")
    public GrassResponse getUserGrass(
        @PathVariable Long userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryUserGrassService.execute(userId, from, to);
    }
}
