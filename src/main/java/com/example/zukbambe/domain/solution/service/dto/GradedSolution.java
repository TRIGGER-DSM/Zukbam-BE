package com.example.zukbambe.domain.solution.service.dto;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;

import java.time.LocalDateTime;

// 채점 반영 트랜잭션이 끝난 뒤에도 지연 로딩 없이 쓸 수 있도록 필요한 값만 꺼내 둔다
public record GradedSolution(
    Long solutionId,
    Long problemId,
    Language language,
    Status status,
    Double time,
    Long memory,
    LocalDateTime submittedAt
) {
    public static GradedSolution from(Solution solution) {
        return new GradedSolution(
            solution.getSolutionId(),
            solution.getProblem().getProblemId(),
            solution.getLanguage(),
            solution.getStatus(),
            solution.getTime(),
            solution.getMemoryUsed(),
            solution.getSubmittedAt()
        );
    }
}
