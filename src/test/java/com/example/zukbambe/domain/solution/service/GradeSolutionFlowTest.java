package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemRank;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.event.PendingSolutionRecoveryListener;
import com.example.zukbambe.domain.solution.exception.TooManySubmissionsException;
import com.example.zukbambe.domain.solution.presentation.dto.request.SubmitSolutionRequest;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.streak.domain.repository.StreakRepository;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class GradeSolutionFlowTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private SubmitSolutionService submitSolutionService;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private PendingSolutionRecoveryListener pendingSolutionRecoveryListener;
    @Autowired
    private SolvedHistoryRepository solvedHistoryRepository;
    @Autowired
    private StreakRepository streakRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;

    private User user;
    private Problem problem;

    @BeforeEach
    void setUp() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        UserRank rank = userRankRepository.save(UserRank.builder().name("rank-" + unique).score(0).build());
        user = userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(rank)
            .build());
        problem = problemRepository.save(Problem.builder()
            .title("problem-" + unique)
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(ProblemRank.LEVEL_1)
            .build());
        testCaseRepository.saveAll(List.of(
            TestCase.of(problem, "1 2", "3", true),
            TestCase.of(problem, "2 3", "5", false)
        ));

        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(user.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 정답이면_결과와_푼_기록_스트릭을_반영한다() {
        given(judgeProvider.judge(any())).willReturn(new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L));

        Solution solution = awaitGraded(submit(Language.CPP17));

        assertThat(solution.getStatus()).isEqualTo(Status.ACCEPTED);
        assertThat(solution.getTime()).isEqualTo(0.012);
        assertThat(solution.getMemoryUsed()).isEqualTo(3000L);
        assertThat(solvedHistoryRepository.existsById(new SolvedHistoryId(user.getUserId(), problem.getProblemId()))).isTrue();
        assertThat(streakRepository.findById(user.getUserId()).orElseThrow().getMaxStreak()).isEqualTo(1);

        ArgumentCaptor<JudgeRequest> captor = ArgumentCaptor.forClass(JudgeRequest.class);
        verify(judgeProvider).judge(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new JudgeRequest(
            JudgeLanguage.CPP17,
            "code",
            List.of(new JudgeCase("1 2", "3"), new JudgeCase("2 3", "5")),
            1.5,
            131072L
        ));
    }

    @Test
    void 오답이면_푼_기록을_남기지_않는다() {
        given(judgeProvider.judge(any())).willReturn(new JudgeResult(JudgeStatus.WRONG_ANSWER, 0.010, 2000L));

        Solution solution = awaitGraded(submit(Language.PYTHON));

        assertThat(solution.getStatus()).isEqualTo(Status.WRONG_ANSWER);
        assertThat(solvedHistoryRepository.existsById(new SolvedHistoryId(user.getUserId(), problem.getProblemId()))).isFalse();
    }

    @Test
    void 채점기가_실패하면_INTERNAL_ERROR로_반영한다() {
        given(judgeProvider.judge(any())).willThrow(new IllegalStateException("judge0 down"));

        Solution solution = awaitGraded(submit(Language.JAVA));

        assertThat(solution.getStatus()).isEqualTo(Status.INTERNAL_ERROR);
        assertThat(solution.getTime()).isNull();
        assertThat(solution.getMemoryUsed()).isNull();
    }

    @Test
    void 테스트케이스가_없으면_채점하지_않고_INTERNAL_ERROR로_반영한다() {
        testCaseRepository.deleteAll(testCaseRepository.findAllByProblemOrderByTestcaseIdAsc(problem));

        Solution solution = awaitGraded(submit(Language.PYTHON));

        assertThat(solution.getStatus()).isEqualTo(Status.INTERNAL_ERROR);
        verify(judgeProvider, never()).judge(any());
    }

    @Test
    void 재시작_전에_PENDING으로_남은_풀이를_다시_채점한다() {
        given(judgeProvider.judge(any())).willReturn(new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L));
        Solution pending = solutionRepository.save(Solution.of(user, problem, Language.PYTHON, "code"));

        pendingSolutionRecoveryListener.recover();

        assertThat(awaitGraded(pending.getSolutionId()).getStatus()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void 일분에_10번을_넘게_제출하면_거부한다() {
        given(judgeProvider.judge(any())).willReturn(new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L));
        SubmitSolutionRequest request = new SubmitSolutionRequest(Language.PYTHON, "code");

        for (int i = 0; i < 10; i++) {
            submitSolutionService.execute(problem.getProblemId(), request);
        }

        assertThatThrownBy(() -> submitSolutionService.execute(problem.getProblemId(), request))
            .isInstanceOf(TooManySubmissionsException.class);
    }

    private Long submit(Language language) {
        submitSolutionService.execute(problem.getProblemId(), new SubmitSolutionRequest(language, "code"));
        return solutionRepository.findAllByFilter(problem.getProblemId(), user.getEmail(), org.springframework.data.domain.Pageable.unpaged())
            .getContent().getFirst().getSolutionId();
    }

    private Solution awaitGraded(Long solutionId) {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            Solution solution = solutionRepository.findById(solutionId).orElseThrow();
            if (solution.getStatus() != Status.PENDING) {
                return solution;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        throw new AssertionError("채점 결과가 반영되지 않았습니다. solutionId=" + solutionId);
    }
}
