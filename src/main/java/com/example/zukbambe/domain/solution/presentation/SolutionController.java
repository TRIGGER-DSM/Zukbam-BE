package com.example.zukbambe.domain.solution.presentation;

import com.example.zukbambe.domain.solution.presentation.dto.request.SubmitSolutionRequest;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionListResponse;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionResponse;
import com.example.zukbambe.domain.solution.service.QuerySolutionDetailService;
import com.example.zukbambe.domain.solution.service.QuerySolutionListService;
import com.example.zukbambe.domain.solution.service.SubmitSolutionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Solution")
@RestController
@RequestMapping("/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SubmitSolutionService submitSolutionService;
    private final QuerySolutionListService querySolutionListService;
    private final QuerySolutionDetailService querySolutionDetailService;

    /**
     * 풀이 제출<p>
     * 채점은 비동기로 진행되니 결과는 풀이 상세 조회로 확인한다. 1분에 10번까지 제출할 수 있다.
     * 생성된 풀이 경로는 Location 헤더로 반환한다.
     */
    @ApiResponse(responseCode = "201")
    @PostMapping("/{problemId}")
    public ResponseEntity<Void> submitSolution(
        @PathVariable Long problemId,
        @RequestBody @Valid SubmitSolutionRequest request
    ) {
        Long solutionId = submitSolutionService.execute(problemId, request);
        URI location = URI.create("/solutions/" + solutionId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 풀이 목록 조회<p>
     * 최신순으로 나오고 problemId, email로 좁혀 볼 수 있다. page는 1부터 시작한다.
     * 다른 유저의 풀이는 선생님이거나 그 문제를 내가 푼 적이 있을 때만 code가 나오고, 아니면 null이다.
     */
    @GetMapping
    public SolutionListResponse getSolutions(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(required = false) Long problemId,
        @RequestParam(required = false) String email
    ) {
        return querySolutionListService.execute(page, size, problemId, email);
    }

    /**
     * 풀이 상세 조회<p>
     * 다른 유저의 풀이는 선생님이거나 그 문제를 내가 푼 적이 있을 때만 code가 나오고, 아니면 null이다.
     * 채점이 아직 안 끝났으면 status가 PENDING이다.
     */
    @GetMapping("/{solutionId}")
    public SolutionResponse getSolution(@PathVariable Long solutionId) {
        return querySolutionDetailService.execute(solutionId);
    }
}
