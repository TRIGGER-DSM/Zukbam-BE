package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.exception.RunQueueFullException;
import com.example.zukbambe.domain.solution.presentation.dto.request.RunSolutionRequest;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class RunSolutionServiceQueueFullTest {

    @Test
    void 풀이_가득_차면_대기열이_찼다고_거부하고_채점기는_부르지_않는다() {
        ProblemFacade problemFacade = mock(ProblemFacade.class);
        JudgeProvider judgeProvider = mock(JudgeProvider.class);
        ThreadPoolTaskExecutor judgeExecutor = mock(ThreadPoolTaskExecutor.class);
        given(problemFacade.getProblemById(1L)).willReturn(
            Problem.builder().title("problem").content("content").timeLimit(1.5).memoryLimit(131072L).build()
        );
        willThrow(new TaskRejectedException("full")).given(judgeExecutor).execute(any());
        RunSolutionService service = new RunSolutionService(
            mock(UserFacade.class), problemFacade, judgeProvider, mock(StringRedisTemplate.class), judgeExecutor
        );

        assertThatThrownBy(() -> service.execute(1L, new RunSolutionRequest(Language.PYTHON, "print(3)", List.of("1 2"))))
            .isInstanceOf(RunQueueFullException.class);
        verifyNoInteractions(judgeProvider);
    }
}
