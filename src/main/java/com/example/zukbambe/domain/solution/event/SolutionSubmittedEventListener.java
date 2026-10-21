package com.example.zukbambe.domain.solution.event;

import com.example.zukbambe.domain.solution.service.ApplyGradingResultService;
import com.example.zukbambe.domain.solution.service.GradeSolutionService;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolutionSubmittedEventListener {

    private final GradeSolutionService gradeSolutionService;
    private final ApplyGradingResultService applyGradingResultService;

    // 커밋 전에 채점을 시작하면 채점 스레드가 Solution을 찾지 못한다
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SolutionSubmittedEvent event) {
        try {
            gradeSolutionService.execute(event.solutionId());
        } catch (TaskRejectedException e) {
            log.error("채점 대기열이 가득 찼습니다. solutionId={}", event.solutionId(), e);
            applyGradingResultService.execute(event.solutionId(), JudgeResult.internalError());
        }
    }
}
