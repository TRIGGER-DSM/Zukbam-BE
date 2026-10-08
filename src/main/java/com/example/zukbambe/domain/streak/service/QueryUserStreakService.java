package com.example.zukbambe.domain.streak.service;

import com.example.zukbambe.domain.streak.domain.repository.StreakRepository;
import com.example.zukbambe.domain.streak.presentation.dto.response.StreakResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueryUserStreakService {

    private final UserFacade userFacade;
    private final StreakRepository streakRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public StreakResponse execute(Long userId) {
        LocalDate today = LocalDate.now(clock);

        // 없는 유저는 0 스트릭이 아니라 404로 응답한다
        userFacade.getUserById(userId);

        // 아직 한 문제도 풀지 않은 유저는 스트릭 row가 없다
        return streakRepository.findById(userId)
            .map(streak -> StreakResponse.of(streak, today))
            .orElseGet(StreakResponse::empty);
    }
}
