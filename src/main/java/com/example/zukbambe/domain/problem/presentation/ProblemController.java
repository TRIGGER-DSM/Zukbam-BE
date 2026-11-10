package com.example.zukbambe.domain.problem.presentation;

import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import com.example.zukbambe.domain.problem.service.CreateProblemService;
import com.example.zukbambe.domain.problem.service.DeleteProblemService;
import com.example.zukbambe.domain.problem.service.QueryProblemDetailService;
import com.example.zukbambe.domain.problem.service.QueryProblemListService;
import com.example.zukbambe.domain.problem.service.UpdateProblemService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Problem")
@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final CreateProblemService createProblemService;
    private final QueryProblemListService queryProblemListService;
    private final QueryProblemDetailService queryProblemDetailService;
    private final UpdateProblemService updateProblemService;
    private final DeleteProblemService deleteProblemService;

    /**
     * 문제 생성<p>
     * TEACHER 권한 필요. 이미 있는 제목은 쓸 수 없고 테스트케이스가 1개 이상 있어야 한다.
     * 생성된 문제 경로는 Location 헤더로 반환한다.
     */
    @ApiResponse(responseCode = "201")
    @PostMapping
    public ResponseEntity<Void> createProblem(@RequestBody @Valid ProblemRequest request) {
        Long problemId = createProblemService.execute(request);
        URI location = URI.create("/problems/" + problemId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 문제 목록 조회<p>
     * 로그인하지 않아도 볼 수 있다. 문제 번호순으로 나온다.
     */
    @GetMapping
    public ProblemListResponse getProblems(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryProblemListService.execute(page, size);
    }

    /**
     * 문제 상세 조회<p>
     * 로그인하지 않아도 볼 수 있다.
     * 예제 테스트케이스만 내려주고, 선생님에게는 채점용 테스트케이스까지 내려준다.
     */
    @GetMapping("/{problemId}")
    public ProblemDetailResponse getProblem(@PathVariable Long problemId) {
        return queryProblemDetailService.execute(problemId);
    }

    /**
     * 문제 수정<p>
     * TEACHER 권한 필요. 테스트케이스는 요청에 담긴 것으로 전부 바뀐다.
     * 문제 랭크를 바꾸면 이미 푼 유저의 XP도 차이만큼 조정된다.
     */
    @PutMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProblem(
        @PathVariable Long problemId,
        @RequestBody @Valid ProblemRequest request
    ) {
        updateProblemService.execute(problemId, request);
    }

    /**
     * 문제 삭제<p>
     * TEACHER 권한 필요. 문제의 테스트케이스도 같이 지워진다.
     */
    @DeleteMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProblem(@PathVariable Long problemId) {
        deleteProblemService.execute(problemId);
    }
}
