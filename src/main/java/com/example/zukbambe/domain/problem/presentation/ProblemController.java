package com.example.zukbambe.domain.problem.presentation;

import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import com.example.zukbambe.domain.problem.service.CreateProblemService;
import com.example.zukbambe.domain.problem.service.DeleteProblemService;
import com.example.zukbambe.domain.problem.service.QueryProblemDetailService;
import com.example.zukbambe.domain.problem.service.QueryProblemListService;
import com.example.zukbambe.domain.problem.service.UpdateProblemService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "문제 생성", description = "TEACHER 권한 필요. 생성된 문제 경로는 Location 헤더로 반환한다.")
    @ApiResponse(responseCode = "201")
    @PostMapping
    public ResponseEntity<Void> createProblem(@RequestBody @Valid ProblemRequest request) {
        Long problemId = createProblemService.execute(request);
        URI location = URI.create("/problems/" + problemId);
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "문제 목록 조회")
    @GetMapping
    public ProblemListResponse getProblems(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryProblemListService.execute(page, size);
    }

    @Operation(summary = "문제 상세 조회")
    @GetMapping("/{problemId}")
    public ProblemDetailResponse getProblem(@PathVariable Long problemId) {
        return queryProblemDetailService.execute(problemId);
    }

    @Operation(summary = "문제 수정", description = "TEACHER 권한 필요.")
    @PutMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProblem(
        @PathVariable Long problemId,
        @RequestBody @Valid ProblemRequest request
    ) {
        updateProblemService.execute(problemId, request);
    }

    @Operation(summary = "문제 삭제", description = "TEACHER 권한 필요.")
    @DeleteMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProblem(@PathVariable Long problemId) {
        deleteProblemService.execute(problemId);
    }
}
