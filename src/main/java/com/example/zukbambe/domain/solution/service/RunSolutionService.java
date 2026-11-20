package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.exception.RunQueueFullException;
import com.example.zukbambe.domain.solution.exception.TooManyRunsException;
import com.example.zukbambe.domain.solution.presentation.dto.request.RunSolutionRequest;
import com.example.zukbambe.domain.solution.presentation.dto.response.RunSolutionResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeRunResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class RunSolutionService {

    private static final int RUN_LIMIT = 10;
    private static final Duration RUN_WINDOW = Duration.ofMinutes(1);
    private static final RedisScript<Long> RUN_COUNT_SCRIPT = RedisScript.of("""
        local count = redis.call('INCR', KEYS[1])
        if redis.call('TTL', KEYS[1]) < 0 then
            redis.call('EXPIRE', KEYS[1], ARGV[1])
        end
        return count
        """, Long.class);

    private final UserFacade userFacade;
    private final ProblemFacade problemFacade;
    private final JudgeProvider judgeProvider;
    private final StringRedisTemplate redisTemplate;
    private final ThreadPoolTaskExecutor judgeExecutor;

    public RunSolutionResponse execute(Long problemId, RunSolutionRequest request) {
        Long userId = userFacade.getCurrentUserId();
        Problem problem = problemFacade.getProblemById(problemId);
        checkRunLimit(userId);

        List<JudgeCase> cases = request.inputs().stream()
            .map(input -> new JudgeCase(input, null))
            .toList();

        JudgeRequest judgeRequest = new JudgeRequest(
            request.language().getJudgeLanguage(),
            request.code(),
            cases,
            problem.getTimeLimit(),
            problem.getMemoryLimit()
        );

        return RunSolutionResponse.from(run(judgeRequest));
    }

    // 채점과 같은 풀을 써서, 실행이 몰려 풀이 가득 차면 대기 중인 정상 제출이 INTERNAL_ERROR로 확정될 수 있다
    private List<JudgeRunResult> run(JudgeRequest judgeRequest) {
        try {
            return CompletableFuture.supplyAsync(() -> judgeProvider.run(judgeRequest), judgeExecutor).join();
        } catch (TaskRejectedException e) {
            throw new RunQueueFullException();
        } catch (CompletionException e) {
            throw e.getCause() instanceof RuntimeException cause ? cause : e;
        }
    }

    private void checkRunLimit(Long userId) {
        Long count = redisTemplate.execute(
            RUN_COUNT_SCRIPT, List.of("solution-run:" + userId), String.valueOf(RUN_WINDOW.toSeconds())
        );
        if (count != null && count > RUN_LIMIT) {
            throw new TooManyRunsException();
        }
    }
}
