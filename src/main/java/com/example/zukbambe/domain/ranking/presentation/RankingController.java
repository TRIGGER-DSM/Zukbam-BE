package com.example.zukbambe.domain.ranking.presentation;

import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import com.example.zukbambe.domain.ranking.service.QueryRankingListService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "랭킹 조회", description = "매일 새벽 3시에 만든 스냅샷 기준이다. 스트릭은 기간과 관계없이 전체 기준으로 응답한다.")
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
