package com.example.zukbambe.domain.quest.facade;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class QuestFacade {

    private final SolvedHistoryRepository solvedHistoryRepository;
    private final SolutionRepository solutionRepository;
    private final Clock clock;

    // 진행도를 따로 저장하지 않고 그날 활동으로 계산해서, 날짜가 바뀌면 저절로 초기화된다
    public DailyActivity getActivity(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return new DailyActivity(
            // solved_time은 서비스 시간대(clock) 기준으로 저장되므로 그대로 비교한다
            solvedHistoryRepository.findSolvedProblemRanksByUserIdAndPeriod(userId, start, end),
            solutionRepository.findSubmissionsByUserIdAndPeriod(userId, toAuditTime(start), toAuditTime(end))
        );
    }

    // submitted_at은 Auditing이 서버 기본 시간대로 저장하므로, 서비스 시간대의 시각을 변환해 비교한다
    private LocalDateTime toAuditTime(LocalDateTime dateTime) {
        return dateTime.atZone(clock.getZone())
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
