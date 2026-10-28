package com.example.zukbambe.domain.solution.presentation.dto.response;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SolutionResponse(
    Long solutionId,
    Language language,
    String code,
    Long memoryUsage,
    Double time,
    Status status,
    LocalDateTime submittedAt
) {
    public static SolutionResponse of(Solution solution, Long currentUserId) {
        boolean isMine = solution.getUser().getUserId().equals(currentUserId);
        return SolutionResponse.builder()
            .solutionId(solution.getSolutionId())
            .language(solution.getLanguage())
            .code(isMine ? solution.getCode() : null)
            .memoryUsage(solution.getMemoryUsed())
            .time(solution.getTime())
            .status(solution.getStatus())
            .submittedAt(solution.getSubmittedAt())
            .build();
    }
}
