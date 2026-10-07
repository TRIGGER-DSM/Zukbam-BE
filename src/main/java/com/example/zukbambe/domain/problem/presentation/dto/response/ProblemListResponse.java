package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ProblemListResponse(
    List<ProblemSummaryResponse> problems,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static ProblemListResponse from(Page<Problem> page) {
        return ProblemListResponse.builder()
            .problems(page.getContent().stream().map(ProblemSummaryResponse::from).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
