package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.exception.TooManyRunsException;
import com.example.zukbambe.domain.solution.presentation.dto.request.RunSolutionRequest;
import com.example.zukbambe.domain.solution.presentation.dto.response.RunSolutionResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeRunResult;
import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RunSolutionServiceTest {

    private static final long PROBLEM_ID = 1L;

    @MockitoBean
    private JudgeProvider judgeProvider;
    @MockitoBean
    private UserFacade userFacade;
    @MockitoBean
    private ProblemFacade problemFacade;

    @Autowired
    private RunSolutionService runSolutionService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private String key;

    @BeforeEach
    void setUp() {
        long userId = ThreadLocalRandom.current().nextLong(1_000_000_000L, Long.MAX_VALUE);
        key = "solution-run:" + userId;
        given(userFacade.getCurrentUserId()).willReturn(userId);
        given(problemFacade.getProblemById(PROBLEM_ID)).willReturn(
            Problem.builder().title("problem").content("content").timeLimit(1.5).memoryLimit(131072L).build()
        );
        given(judgeProvider.run(any())).willReturn(
            List.of(new JudgeRunResult(JudgeStatus.ACCEPTED, "3\n", null, null, 0.01, 1000L))
        );
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(key);
    }

    @Test
    void 문제의_제한과_입력으로_실행하고_결과를_돌려준다() {
        RunSolutionResponse response = runSolutionService.execute(PROBLEM_ID, request());

        ArgumentCaptor<JudgeRequest> captor = ArgumentCaptor.forClass(JudgeRequest.class);
        verify(judgeProvider).run(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new JudgeRequest(
            JudgeLanguage.PYTHON, "print(3)", List.of(new JudgeCase("1 2", null)), 1.5, 131072L
        ));
        assertThat(response.results()).containsExactly(
            new RunSolutionResponse.Result(Status.ACCEPTED, "3\n", null, null, 1000L, 0.01)
        );
    }

    @Test
    void 일분에_10번을_넘게_실행하면_거부한다() {
        for (int i = 0; i < 10; i++) {
            runSolutionService.execute(PROBLEM_ID, request());
        }

        assertThatThrownBy(() -> runSolutionService.execute(PROBLEM_ID, request()))
            .isInstanceOf(TooManyRunsException.class);
    }

    @Test
    void 횟수를_세면_만료_시간이_걸린다() {
        runSolutionService.execute(PROBLEM_ID, request());

        assertThat(redisTemplate.getExpire(key)).isPositive();
    }

    @Test
    void 만료_시간이_없는_키가_남아_있어도_실행하면_만료_시간이_걸린다() {
        redisTemplate.opsForValue().set(key, "5");
        assertThat(redisTemplate.getExpire(key)).isEqualTo(-1L);

        runSolutionService.execute(PROBLEM_ID, request());

        assertThat(redisTemplate.getExpire(key)).isPositive();
    }

    private RunSolutionRequest request() {
        return new RunSolutionRequest(Language.PYTHON, "print(3)", List.of("1 2"));
    }
}
