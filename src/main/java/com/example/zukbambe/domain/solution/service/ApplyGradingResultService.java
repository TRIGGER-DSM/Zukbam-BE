package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.exception.SolutionNotFoundException;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.streak.facade.StreakFacade;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyGradingResultService {

    private final SolutionRepository solutionRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final StreakFacade streakFacade;

    // 제출 트랜잭션의 커밋 이후에도 불릴 수 있어서 항상 새 트랜잭션으로 반영한다
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long solutionId, JudgeResult result) {
        Solution solution = solutionRepository.findById(solutionId)
            .orElseThrow(SolutionNotFoundException::new);
        Status status = Status.from(result.status());
        solution.grade(status, result.time(), result.memory());

        SolvedHistoryId solvedHistoryId = new SolvedHistoryId(
            solution.getUser().getUserId(),
            solution.getProblem().getProblemId()
        );
        if (status == Status.ACCEPTED && !solvedHistoryRepository.existsById(solvedHistoryId)) {
            solvedHistoryRepository.save(SolvedHistory.of(solution.getUser(), solution.getProblem()));
            streakFacade.recordSolved(solution.getUser());
        }
    }
}
