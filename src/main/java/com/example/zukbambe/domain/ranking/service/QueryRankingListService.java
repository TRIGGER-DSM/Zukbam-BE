package com.example.zukbambe.domain.ranking.service;

import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.domain.repository.RankingSnapshotRepository;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryRankingListService {

    private final RankingSnapshotRepository rankingSnapshotRepository;

    @Transactional(readOnly = true)
    public RankingListResponse execute(RankingMetric metric, RankingPeriod period, boolean excludeTeachers, int page, int size) {
        // 스트릭은 기간 구분이 없어서 어떤 기간을 요청해도 전체 기준으로 응답한다
        RankingPeriod effectivePeriod = metric == RankingMetric.STREAK ? RankingPeriod.ALL : period;

        return RankingListResponse.from(rankingSnapshotRepository.findRanking(
            metric.name(), effectivePeriod.name(), excludeTeachers, PageRequest.of(page, size)
        ));
    }
}
