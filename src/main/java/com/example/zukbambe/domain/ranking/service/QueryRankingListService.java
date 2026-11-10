package com.example.zukbambe.domain.ranking.service;

import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.domain.repository.RankingRepository;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class QueryRankingListService {

    // 전체 기간도 같은 쿼리를 쓰기 위한 시작 시각
    private static final LocalDateTime BEGINNING = LocalDateTime.of(1970, 1, 1, 0, 0);

    private final RankingRepository rankingRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RankingListResponse execute(RankingMetric metric, RankingPeriod period, boolean excludeTeachers, int page, int size) {
        // 스트릭은 기간 구분이 없어서 어떤 기간을 요청해도 전체 기준으로 응답한다
        RankingPeriod effectivePeriod = metric == RankingMetric.STREAK ? RankingPeriod.ALL : period;
        LocalDate today = LocalDate.now(clock);

        return RankingListResponse.from(rankingRepository.findRanking(
            metric.name(), effectivePeriod.name(), startOf(effectivePeriod, today), today, excludeTeachers, PageRequest.of(page, size)
        ));
    }

    // 달력 기준이라 주간은 이번 주 월요일 0시, 월간은 이번 달 1일 0시부터 센다
    private LocalDateTime startOf(RankingPeriod period, LocalDate today) {
        return switch (period) {
            case WEEKLY -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            case MONTHLY -> today.withDayOfMonth(1).atStartOfDay();
            case ALL -> BEGINNING;
        };
    }
}
