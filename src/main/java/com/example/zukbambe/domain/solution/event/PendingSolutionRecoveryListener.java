package com.example.zukbambe.domain.solution.event;

import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.service.ApplyGradingResultService;
import com.example.zukbambe.domain.solution.service.GradeSolutionService;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingSolutionRecoveryListener {

    private final SolutionRepository solutionRepository;
    private final GradeSolutionService gradeSolutionService;
    private final ApplyGradingResultService applyGradingResultService;

    // 채점 대기열은 메모리에만 있어서, 재시작 전에 PENDING으로 남은 풀이는 다시 채점한다
    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        List<Long> solutionIds = solutionRepository.findIdsByStatus(Status.PENDING);
        if (!solutionIds.isEmpty()) {
            log.info("PENDING 풀이를 다시 채점합니다. count={}", solutionIds.size());
        }

        for (Long solutionId : solutionIds) {
            try {
                gradeSolutionService.execute(solutionId);
            } catch (TaskRejectedException e) {
                log.error("채점 대기열이 가득 찼습니다. solutionId={}", solutionId, e);
                applyGradingResultService.execute(solutionId, JudgeResult.internalError());
            }
        }
    }
}
