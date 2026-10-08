package com.example.zukbambe.domain.streak.facade;

import com.example.zukbambe.domain.streak.domain.Streak;
import com.example.zukbambe.domain.streak.domain.repository.StreakRepository;
import com.example.zukbambe.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StreakFacade {

    private final StreakRepository streakRepository;
    private final Clock clock;

    @Transactional
    public void recordSolved(User user) {
        // row를 먼저 보장한 뒤 잠그고 읽어, 같은 유저의 동시 갱신을 순서대로 처리한다
        streakRepository.insertIfAbsent(user.getUserId());
        Streak streak = streakRepository.findWithLockByUserId(user.getUserId())
            .orElseThrow();

        streak.recordSolved(LocalDate.now(clock));
    }
}
