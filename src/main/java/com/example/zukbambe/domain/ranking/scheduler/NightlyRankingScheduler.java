package com.example.zukbambe.domain.ranking.scheduler;

import com.example.zukbambe.domain.ranking.service.CreateRankingSnapshotService;
import com.example.zukbambe.domain.xp.service.RecalculateAllXpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NightlyRankingScheduler {

    private final RecalculateAllXpService recalculateAllXpService;
    private final CreateRankingSnapshotService createRankingSnapshotService;

    // 스냅샷이 재계산된 XP와 랭크를 담도록 재계산을 먼저 한다
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void run() {
        try {
            recalculateAllXpService.execute();
        } catch (RuntimeException e) {
            // 재계산이 실패해도 랭킹은 매일 갱신되어야 하므로 스냅샷은 진행한다
            log.error("전체 XP 재계산에 실패했습니다.", e);
        }
        createRankingSnapshotService.execute();
    }
}
