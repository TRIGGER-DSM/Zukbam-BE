package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.exception.TooManyRunsException;
import com.example.zukbambe.domain.solution.presentation.dto.request.RunSolutionRequest;
import com.example.zukbambe.domain.solution.presentation.dto.response.RunSolutionResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunSolutionService {

    private static final int RUN_LIMIT = 10;
    private static final Duration RUN_WINDOW = Duration.ofMinutes(1);

    private final UserFacade userFacade;
    private final ProblemFacade problemFacade;
    private final JudgeProvider judgeProvider;
    private final StringRedisTemplate redisTemplate;

    // 실행이 끝날 때까지 기다리므로 트랜잭션(DB 커넥션)을 잡지 않는다
    public RunSolutionResponse execute(Long problemId, RunSolutionRequest request) {
        Long userId = userFacade.getCurrentUserId();
        Problem problem = problemFacade.getProblemById(problemId);
        checkRunLimit(userId);

        List<JudgeCase> cases = request.inputs().stream()
            .map(input -> new JudgeCase(input, null))
            .toList();

        return RunSolutionResponse.from(judgeProvider.run(new JudgeRequest(
            request.language().getJudgeLanguage(),
            request.code(),
            cases,
            problem.getTimeLimit(),
            problem.getMemoryLimit()
        )));
    }

    private void checkRunLimit(Long userId) {
        String key = "solution-run:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, RUN_WINDOW);
        }
        if (count != null && count > RUN_LIMIT) {
            throw new TooManyRunsException();
        }
    }
}
