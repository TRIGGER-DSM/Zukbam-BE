package com.example.zukbambe.domain.streak.presentation;

import com.example.zukbambe.domain.streak.presentation.dto.response.GrassResponse;
import com.example.zukbambe.domain.streak.presentation.dto.response.StreakResponse;
import com.example.zukbambe.domain.streak.service.QueryGrassService;
import com.example.zukbambe.domain.streak.service.QueryStreakService;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Streak")
@RestController
@RequestMapping("/streaks")
@RequiredArgsConstructor
public class StreakController {

    private final QueryStreakService queryStreakService;
    private final QueryGrassService queryGrassService;

    /**
     * 내 스트릭 조회<p>
     * 어제도 오늘도 문제를 풀지 않았다면 현재 스트릭은 0이다.
     * 아직 한 문제도 풀지 않은 유저는 전부 0으로 나온다.
     */
    @GetMapping("/me")
    public StreakResponse getMyStreak(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return queryStreakService.execute(userDetails.userId());
    }

    /**
     * 내 잔디 조회<p>
     * 날짜별로 처음 맞힌 문제 수를 내려준다. 문제를 푼 날만 들어 있다.
     * from, to는 yyyy-MM-dd 형식이고, 생략하면 오늘까지 최근 1년을 조회한다. 1년이 넘는 기간은 조회할 수 없다.
     */
    @GetMapping("/me/grass")
    public GrassResponse getMyGrass(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryGrassService.execute(userDetails.userId(), from, to);
    }

    /**
     * 유저 스트릭 조회<p>
     * 어제도 오늘도 문제를 풀지 않았다면 현재 스트릭은 0이다.
     * 아직 한 문제도 풀지 않은 유저는 전부 0으로 나온다.
     */
    @GetMapping("/{userId}")
    public StreakResponse getUserStreak(@PathVariable Long userId) {
        return queryStreakService.execute(userId);
    }

    /**
     * 유저 잔디 조회<p>
     * 날짜별로 처음 맞힌 문제 수를 내려준다. 문제를 푼 날만 들어 있다.
     * from, to는 yyyy-MM-dd 형식이고, 생략하면 오늘까지 최근 1년을 조회한다. 1년이 넘는 기간은 조회할 수 없다.
     */
    @GetMapping("/{userId}/grass")
    public GrassResponse getUserGrass(
        @PathVariable Long userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return queryGrassService.execute(userId, from, to);
    }
}
