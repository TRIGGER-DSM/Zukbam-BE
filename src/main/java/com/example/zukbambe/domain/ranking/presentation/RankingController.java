package com.example.zukbambe.domain.ranking.presentation;

import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import com.example.zukbambe.domain.ranking.service.QueryRankingListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ranking")
@RestController
@RequestMapping("/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final QueryRankingListService queryRankingListService;

    /**
     * 랭킹 조회<p>
     * metric으로 정렬 기준(XP, 푼 문제 수, 스트릭)을, period로 집계 기간(주간, 월간, 전체)을 고른다.
     * 주간은 이번 주 월요일 0시부터, 월간은 이번 달 1일 0시부터 센다. 미리 계산해 두지 않고 조회할 때 바로 집계한다.
     * 스트릭은 기간을 무엇으로 주든 전체 기준으로 나온다.
     */
    @GetMapping
    public RankingListResponse getRankings(
        @RequestParam(defaultValue = "XP") RankingMetric metric,
        @RequestParam(defaultValue = "ALL") RankingPeriod period,
        @RequestParam(defaultValue = "false") boolean excludeTeachers,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryRankingListService.execute(metric, period, excludeTeachers, page, size);
    }
}
