package com.example.zukbambe.domain.ranking.service;

import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.domain.repository.RankingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class CreateRankingSnapshotService {

    // 전체 기간도 같은 쿼리를 쓰기 위한 시작 시각
    private static final LocalDateTime BEGINNING = LocalDateTime.of(1970, 1, 1, 0, 0);

    private final RankingSnapshotRepository rankingSnapshotRepository;
    private final Clock clock;

    // 지우고 다시 넣는 동안 한 트랜잭션이라 조회 쪽에는 이전 스냅샷이나 새 스냅샷만 보인다
    @Transactional
    public void execute() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();

        rankingSnapshotRepository.deleteAllInBatch();
        insert(RankingPeriod.WEEKLY, today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(), now, today);
        insert(RankingPeriod.MONTHLY, today.withDayOfMonth(1).atStartOfDay(), now, today);
        insert(RankingPeriod.ALL, BEGINNING, now, today);
    }

    private void insert(RankingPeriod period, LocalDateTime start, LocalDateTime now, LocalDate today) {
        rankingSnapshotRepository.insertSnapshot(period.name(), start, now, today);
    }
}
