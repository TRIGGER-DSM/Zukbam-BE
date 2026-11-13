package com.example.zukbambe.domain.solution.presentation.dto.response;

import com.example.zukbambe.domain.solution.domain.Solution;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Predicate;

@Builder
public record SolutionListResponse(
    List<SolutionResponse> solutions,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static SolutionListResponse of(Page<Solution> page, Predicate<Solution> codeVisible) {
        return SolutionListResponse.builder()
            .solutions(page.getContent().stream().map(solution -> SolutionResponse.of(solution, codeVisible.test(solution))).toList())
            .page(page.getNumber() + 1)
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
