package com.example.zukbambe.domain.quest.facade;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class QuestFacade {

    private final SolvedHistoryRepository solvedHistoryRepository;
    private final SolutionRepository solutionRepository;

    // 진행도를 따로 저장하지 않고 그날 활동으로 계산해서, 날짜가 바뀌면 저절로 초기화된다
    public DailyActivity getActivity(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        // solved_time과 submitted_at 모두 서비스 시계(한국 시간)로 저장되므로 그대로 비교한다
        return new DailyActivity(
            solvedHistoryRepository.findSolvedProblemRanksByUserIdAndPeriod(userId, start, end),
            solutionRepository.findSubmissionsByUserIdAndPeriod(userId, start, end)
        );
    }
}
