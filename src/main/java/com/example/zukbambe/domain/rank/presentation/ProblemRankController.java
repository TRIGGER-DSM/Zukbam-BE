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
     * XP가 낮은 순으로 나온다. XP가 높을수록 어려운 난이도다.
     */
    @GetMapping
    public ProblemRankListResponse getProblemRanks() {
        return queryProblemRankListService.execute();
    }

    /**
     * 문제 랭크 수정<p>
     * TEACHER 권한 필요. 이미 있는 이름은 쓸 수 없다.
     * XP를 바꾸면 그 랭크 문제를 푼 유저의 XP도 차이만큼 조정된다.
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
