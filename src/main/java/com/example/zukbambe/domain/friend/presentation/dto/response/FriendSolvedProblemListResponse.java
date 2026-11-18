package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Builder
public record FriendSolvedProblemListResponse(
    List<FriendSolvedProblemResponse> problems,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static FriendSolvedProblemListResponse of(
        Page<Long> page,
        Map<Long, List<SolvedHistory>> solvedHistoriesByProblemId
    ) {
        return FriendSolvedProblemListResponse.builder()
            .problems(page.getContent().stream()
                .map(problemId -> FriendSolvedProblemResponse.from(solvedHistoriesByProblemId.get(problemId)))
                .toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
