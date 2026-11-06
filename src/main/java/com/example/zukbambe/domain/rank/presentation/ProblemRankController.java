package com.example.zukbambe.domain.rank.presentation;

import com.example.zukbambe.domain.rank.presentation.dto.request.UpdateProblemRankRequest;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankListResponse;
import com.example.zukbambe.domain.rank.service.QueryProblemRankListService;
import com.example.zukbambe.domain.rank.service.UpdateProblemRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ProblemRank")
@RestController
@RequestMapping("/problem-ranks")
@RequiredArgsConstructor
public class ProblemRankController {

    private final QueryProblemRankListService queryProblemRankListService;
    private final UpdateProblemRankService updateProblemRankService;

    @Operation(summary = "문제 랭크 목록 조회")
    @GetMapping
    public ProblemRankListResponse getProblemRanks() {
        return queryProblemRankListService.execute();
    }

    @Operation(summary = "문제 랭크 수정", description = "TEACHER 권한 필요. XP가 바뀌면 그 랭크 문제를 푼 유저에게 차액을 지급한다.")
    @PutMapping("/{problemRankId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProblemRank(
        @PathVariable Long problemRankId,
        @RequestBody @Valid UpdateProblemRankRequest request
    ) {
        updateProblemRankService.execute(problemRankId, request);
    }
}
