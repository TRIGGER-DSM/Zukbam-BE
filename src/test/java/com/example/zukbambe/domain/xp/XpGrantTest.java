package com.example.zukbambe.domain.xp;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.service.ApplyGradingResultService;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.xp.domain.XpHistory;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class XpGrantTest {

    private static final JudgeResult ACCEPTED = new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L);

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private ApplyGradingResultService applyGradingResultService;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;
    @Autowired
    private XpHistoryRepository xpHistoryRepository;

    private String unique;
    private User user;

    @BeforeEach
    void setUp() {
        unique = UUID.randomUUID().toString().substring(0, 8);
        user = userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
    }

    @Test
    void 처음_맞히면_문제_랭크만큼_XP와_내역이_쌓인다() {
        Problem problem = problemOf("물 한 컵");

        applyAccepted(problem);

        assertThat(reloadUser().getXp()).isEqualTo(20L);
        List<XpHistory> histories = historiesOfUser();
        assertThat(histories).hasSize(1);
        XpHistory history = histories.getFirst();
        assertThat(history.getAmount()).isEqualTo(20L);
        assertThat(history.getReason()).isEqualTo("문제 풀이");
        assertThat(history.getProblemId()).isEqualTo(problem.getProblemId());
        assertThat(history.getProblemTitle()).isEqualTo(problem.getTitle());
        assertThat(history.getCreatedAt()).isNotNull();
    }

    @Test
    void 같은_문제를_다시_맞히면_XP를_주지_않는다() {
        Problem problem = problemOf("물 한 컵");

        applyAccepted(problem);
        applyAccepted(problem);

        assertThat(reloadUser().getXp()).isEqualTo(20L);
        assertThat(historiesOfUser()).hasSize(1);
    }

    @Test
    void XP가_기준을_넘으면_랭크가_오른다() {
        applyAccepted(problemOf("비"));
        applyAccepted(problemOf("비"));

        assertThat(rankNameOf(reloadUser())).isEqualTo("대나무 1m");
    }

    @Test
    void 서로_다른_문제를_동시에_맞혀도_XP와_랭크가_모두_반영된다() throws Exception {
        List<Solution> solutions = List.of(
            solutionRepository.save(Solution.of(user, problemOf("비"), Language.PYTHON, "code")),
            solutionRepository.save(Solution.of(user, problemOf("비"), Language.PYTHON, "code"))
        );
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<?>> futures = solutions.stream()
            .map(solution -> executor.submit(() -> {
                start.await();
                applyGradingResultService.execute(solution.getSolutionId(), ACCEPTED);
                return null;
            }))
            .<Future<?>>map(future -> future)
            .toList();
        start.countDown();
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        User reloaded = reloadUser();
        assertThat(reloaded.getXp()).isEqualTo(160L);
        assertThat(rankNameOf(reloaded)).isEqualTo("대나무 1m");
        assertThat(historiesOfUser()).hasSize(2);
    }

    private Problem problemOf(String rankName) {
        return problemRepository.save(Problem.builder()
            .title("problem-" + UUID.randomUUID())
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName(rankName).orElseThrow())
            .build());
    }

    private void applyAccepted(Problem problem) {
        Solution solution = solutionRepository.save(Solution.of(user, problem, Language.PYTHON, "code"));
        applyGradingResultService.execute(solution.getSolutionId(), ACCEPTED);
    }

    private User reloadUser() {
        return userRepository.findById(user.getUserId()).orElseThrow();
    }

    private String rankNameOf(User target) {
        Long rankId = target.getRank().getUserRankId();
        return userRankRepository.findById(rankId).orElseThrow().getName();
    }

    private List<XpHistory> historiesOfUser() {
        return xpHistoryRepository.findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(user.getUserId(), Pageable.unpaged())
            .getContent();
    }
}
