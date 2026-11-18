package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemSolveStatus;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Builder
public record ProblemListResponse(
    List<ProblemSummaryResponse> problems,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static ProblemListResponse of(
        Page<Problem> page,
        Map<Long, List<CategoryResponse>> categoriesByProblemId,
        Map<Long, Long> solverCountByProblemId,
        Map<Long, Double> averageAttemptsByProblemId,
        Map<Long, ProblemSolveStatus> statusByProblemId
    ) {
        return ProblemListResponse.builder()
            .problems(page.getContent().stream()
                .map(problem -> ProblemSummaryResponse.of(
                    problem,
                    categoriesByProblemId.getOrDefault(problem.getProblemId(), List.of()),
                    solverCountByProblemId.getOrDefault(problem.getProblemId(), 0L),
                    averageAttemptsByProblemId.get(problem.getProblemId()),
                    statusByProblemId.get(problem.getProblemId())
                ))
                .toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
