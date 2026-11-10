package com.example.zukbambe.domain.quest.service;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.quest.presentation.dto.response.QuestListResponse;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class QueryTodayQuestListService {

    private final UserFacade userFacade;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final SolutionRepository solutionRepository;
    private final ProblemRankRepository problemRankRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public QuestListResponse execute() {
        Long userId = userFacade.getCurrentUserId();
        LocalDate today = LocalDate.now(clock);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime resetAt = start.plusDays(1);

        // 진행도를 따로 저장하지 않고 오늘 활동으로 계산해서, 날짜가 바뀌면 저절로 초기화된다
        DailyActivity activity = new DailyActivity(
            // solved_time은 서비스 시간대(clock) 기준으로 저장되므로 그대로 비교한다
            solvedHistoryRepository.findSolvedProblemRanksByUserIdAndPeriod(userId, start, resetAt),
            solutionRepository.findSubmissionsByUserIdAndPeriod(userId, toAuditTime(start), toAuditTime(resetAt))
        );

        return QuestListResponse.of(
            DailyQuest.of(today), activity, problemRankRepository.findAllByOrderByXpAscProblemRankIdAsc(), resetAt
        );
    }

    // submitted_at은 Auditing이 서버 기본 시간대로 저장하므로, 서비스 시간대의 시각을 변환해 비교한다
    private LocalDateTime toAuditTime(LocalDateTime dateTime) {
        return dateTime.atZone(clock.getZone())
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
