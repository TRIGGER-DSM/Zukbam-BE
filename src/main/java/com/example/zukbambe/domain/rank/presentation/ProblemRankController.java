package com.example.zukbambe.domain.rank.presentation;

import com.example.zukbambe.domain.rank.presentation.dto.request.UpdateProblemRankRequest;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankListResponse;
import com.example.zukbambe.domain.rank.service.QueryProblemRankListService;
import com.example.zukbambe.domain.rank.service.UpdateProblemRankService;
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

    /**
     * 문제 랭크 목록 조회<p>
     * XP가 낮은 순으로 조회한다. 난이도 순서는 XP 순서와 같다.
     */
    @GetMapping
    public ProblemRankListResponse getProblemRanks() {
        return queryProblemRankListService.execute();
    }

    /**
     * 문제 랭크 수정<p>
     * TEACHER 권한 필요. 이름은 중복될 수 없다.
     * XP가 바뀌면 그 랭크 문제를 푼 유저의 XP에 차액을 반영한다.
     */
    @PutMapping("/{problemRankId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProblemRank(
        @PathVariable Long problemRankId,
        @RequestBody @Valid UpdateProblemRankRequest request
    ) {
        updateProblemRankService.execute(problemRankId, request);
    }
}
