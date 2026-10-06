package com.example.zukbambe.domain.problem.presentation;

import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemIdResponse;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import com.example.zukbambe.domain.problem.service.CreateProblemService;
import com.example.zukbambe.domain.problem.service.DeleteProblemService;
import com.example.zukbambe.domain.problem.service.QueryProblemDetailService;
import com.example.zukbambe.domain.problem.service.QueryProblemListService;
import com.example.zukbambe.domain.problem.service.UpdateProblemService;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final CreateProblemService createProblemService;
    private final QueryProblemListService queryProblemListService;
    private final QueryProblemDetailService queryProblemDetailService;
    private final UpdateProblemService updateProblemService;
    private final DeleteProblemService deleteProblemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemIdResponse createProblem(@RequestBody @Valid ProblemRequest request) {
        return createProblemService.execute(request);
    }

    @GetMapping
    public ProblemListResponse getProblems(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryProblemListService.execute(page, size);
    }

    @GetMapping("/{problemId}")
    public ProblemDetailResponse getProblem(
        @PathVariable Long problemId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return queryProblemDetailService.execute(problemId, userDetails.role());
    }

    @PutMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProblem(
        @PathVariable Long problemId,
        @RequestBody @Valid ProblemRequest request
    ) {
        updateProblemService.execute(problemId, request);
    }

    @DeleteMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProblem(@PathVariable Long problemId) {
        deleteProblemService.execute(problemId);
    }
}
