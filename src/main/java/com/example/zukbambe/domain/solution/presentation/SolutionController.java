package com.example.zukbambe.domain.solution.presentation;

import com.example.zukbambe.domain.solution.presentation.dto.request.SubmitSolutionRequest;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionListResponse;
import com.example.zukbambe.domain.solution.service.QuerySolutionListService;
import com.example.zukbambe.domain.solution.service.SubmitSolutionService;
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

@RestController
@RequestMapping("/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SubmitSolutionService submitSolutionService;
    private final QuerySolutionListService querySolutionListService;

    @PostMapping("/{problemId}")
    public ResponseEntity<Void> submitSolution(
        @PathVariable Long problemId,
        @RequestBody @Valid SubmitSolutionRequest request
    ) {
        URI location = submitSolutionService.execute(problemId, request);
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public SolutionListResponse getSolutions(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(required = false) Long problemId,
        @RequestParam(required = false) String email
    ) {
        return querySolutionListService.execute(page, size, problemId, email);
    }
}
