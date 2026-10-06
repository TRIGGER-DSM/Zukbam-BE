package com.example.zukbambe.domain.problem.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProblemListResponse(
    List<ProblemSummaryResponse> problems,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static ProblemListResponse from(Page<Problem> page) {
        return new ProblemListResponse(
            page.getContent().stream().map(ProblemSummaryResponse::from).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
