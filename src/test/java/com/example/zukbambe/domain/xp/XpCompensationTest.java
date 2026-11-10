package com.example.zukbambe.domain.xp;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.request.TestCaseRequest;
import com.example.zukbambe.domain.problem.service.UpdateProblemService;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class XpCompensationTest {

    private static final JudgeResult ACCEPTED = new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L);

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private UpdateProblemService updateProblemService;
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

    private User solver;
    private User nonSolver;

    @BeforeEach
    void setUp() {
        solver = newUser();
        nonSolver = newUser();
    }

    @Test
    void 문제_랭크가_오르면_푼_사람에게만_차액을_준다() {
        Problem problem = problemOf("이슬");
        solve(solver, problem);

        updateProblemService.execute(problem.getProblemId(), requestWithRank(problem, "물 한 병"));

        assertThat(xpOf(solver)).isEqualTo(30L);
        assertThat(xpOf(nonSolver)).isEqualTo(0L);
        XpHistory last = historiesOf(solver).getFirst();
        assertThat(last.getAmount()).isEqualTo(20L);
        assertThat(last.getReason()).isEqualTo("문제 난이도 변경");
        assertThat(last.getProblemId()).isEqualTo(problem.getProblemId());
        assertThat(historiesOf(nonSolver)).isEmpty();
    }

    @Test
    void 문제_랭크가_내려가면_XP를_빼고_랭크도_내려간다() {
        Problem rain = problemOf("비");
        solve(solver, rain);
        solve(solver, problemOf("이슬"));
        solve(solver, problemOf("이슬"));
        assertThat(rankNameOf(solver)).isEqualTo("대나무 1m");

        updateProblemService.execute(rain.getProblemId(), requestWithRank(rain, "이슬"));

        assertThat(xpOf(solver)).isEqualTo(30L);
        assertThat(rankNameOf(solver)).isEqualTo("죽순");
        assertThat(historiesOf(solver).getFirst().getAmount()).isEqualTo(-70L);
    }

    @Test
    void 랭크를_안_바꾸면_보상_내역이_생기지_않는다() {
        Problem problem = problemOf("이슬");
        solve(solver, problem);

        updateProblemService.execute(problem.getProblemId(), requestWithRank(problem, "이슬"));

        assertThat(historiesOf(solver)).hasSize(1);
        assertThat(xpOf(solver)).isEqualTo(10L);
    }

    private User newUser() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
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

    private void solve(User user, Problem problem) {
        Solution solution = solutionRepository.save(Solution.of(user, problem, Language.PYTHON, "code"));
        applyGradingResultService.execute(solution.getSolutionId(), ACCEPTED);
    }

    private ProblemRequest requestWithRank(Problem problem, String rankName) {
        return new ProblemRequest(problem.getTitle(), "content", 131072L, 1.5,
            problemRankRepository.findByName(rankName).orElseThrow().getProblemRankId(),
            List.of(new TestCaseRequest("1 2", "3", true)));
    }

    private long xpOf(User user) {
        return userRepository.findById(user.getUserId()).orElseThrow().getXp();
    }

    private String rankNameOf(User user) {
        Long rankId = userRepository.findById(user.getUserId()).orElseThrow().getRank().getUserRankId();
        return userRankRepository.findById(rankId).orElseThrow().getName();
    }

    private List<XpHistory> historiesOf(User user) {
        return xpHistoryRepository.findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(user.getUserId(), Pageable.unpaged())
            .getContent();
    }
}
