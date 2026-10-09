package com.example.zukbambe.domain.streak.presentation;

import com.example.zukbambe.domain.streak.presentation.dto.response.GrassResponse;
import com.example.zukbambe.domain.streak.presentation.dto.response.StreakResponse;
import com.example.zukbambe.domain.streak.service.QueryGrassService;
import com.example.zukbambe.domain.streak.service.QueryStreakService;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final QueryStreakService queryStreakService;
    private final QueryGrassService queryGrassService;

    @GetMapping("/me")
    public StreakResponse getMyStreak(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return queryStreakService.execute(userDetails.userId());
    }

    @GetMapping("/me/grass")
    public GrassResponse getMyGrass(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryGrassService.execute(userDetails.userId(), from, to);
    }

    @GetMapping("/{userId}")
    public StreakResponse getUserStreak(@PathVariable Long userId) {
        return queryStreakService.execute(userId);
    }

    @GetMapping("/{userId}/grass")
    public GrassResponse getUserGrass(
        @PathVariable Long userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryGrassService.execute(userId, from, to);
    }
}
