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
        Streak streak = streakRepository.findById(user.getUserId())
            .orElseGet(() -> streakRepository.save(Streak.of(user)));

        streak.recordSolved(LocalDate.now(clock));
    }
}
