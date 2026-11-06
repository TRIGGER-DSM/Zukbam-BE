package com.example.zukbambe.domain.rank.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.rank.exception.ProblemRankNameDuplicatedException;
import com.example.zukbambe.domain.rank.presentation.dto.request.UpdateProblemRankRequest;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UpdateProblemRankServiceTest {

    private static final JudgeResult ACCEPTED = new JudgeResult(JudgeStatus.ACCEPTED, 0.012, 3000L);
    private static final String CUP = "물 한 컵";

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private UpdateProblemRankService updateProblemRankService;
    @Autowired
    private QueryProblemRankListService queryProblemRankListService;
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

    private Long cupRankId;
    private User solver;

    @BeforeEach
    void setUp() {
        ProblemRank cup = problemRankRepository.findByName(CUP).orElseThrow();
        cupRankId = cup.getProblemRankId();
        solver = userRepository.save(User.builder()
            .email(UUID.randomUUID().toString().substring(0, 8) + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
        solve(problemOf(cup));
        solve(problemOf(cup));
    }

    // 시드 값을 다른 테스트도 쓰므로 서비스로 되돌려 다른 유저 XP까지 원래대로 맞춘다
    @AfterEach
    void tearDown() {
        updateProblemRankService.execute(cupRankId, new UpdateProblemRankRequest(CUP, 20L));
    }

    @Test
    void 난이도_XP를_바꾸면_그_난이도_문제를_푼_만큼_차액을_준다() {
        updateProblemRankService.execute(cupRankId, new UpdateProblemRankRequest(CUP, 25L));

        assertThat(xpOf(solver)).isEqualTo(50L);
        assertThat(historiesOf(solver)).filteredOn(h -> h.getReason().equals("난이도 XP 변경")).hasSize(2)
            .allSatisfy(h -> assertThat(h.getAmount()).isEqualTo(5L));
        assertThat(problemRankRepository.findById(cupRankId).orElseThrow().getXp()).isEqualTo(25L);
    }

    @Test
    void 이름만_바꾸면_보상하지_않는다() {
        updateProblemRankService.execute(cupRankId, new UpdateProblemRankRequest("물 한 잔", 20L));

        assertThat(historiesOf(solver)).hasSize(2);
        assertThat(xpOf(solver)).isEqualTo(40L);
        assertThat(problemRankRepository.findById(cupRankId).orElseThrow().getName()).isEqualTo("물 한 잔");
    }

    @Test
    void 다른_랭크와_이름이_겹치면_실패한다() {
        assertThatThrownBy(() -> updateProblemRankService.execute(cupRankId, new UpdateProblemRankRequest("이슬", 20L)))
            .isInstanceOf(ProblemRankNameDuplicatedException.class);
    }

    @Test
    void 문제_랭크_목록은_XP_순서대로_나온다() {
        List<ProblemRankResponse> ranks = queryProblemRankListService.execute().problemRanks();

        assertThat(ranks).extracting(ProblemRankResponse::name)
            .containsSubsequence("이슬", CUP, "물 한 병", "물 한 통", "비");
        assertThat(ranks).extracting(ProblemRankResponse::xp).isSorted();
    }

    private Problem problemOf(ProblemRank rank) {
        return problemRepository.save(Problem.builder()
            .title("problem-" + UUID.randomUUID())
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(rank)
            .build());
    }

    private void solve(Problem problem) {
        Solution solution = solutionRepository.save(Solution.of(solver, problem, Language.PYTHON, "code"));
        applyGradingResultService.execute(solution.getSolutionId(), ACCEPTED);
    }

    private long xpOf(User user) {
        return userRepository.findById(user.getUserId()).orElseThrow().getXp();
    }

    private List<XpHistory> historiesOf(User user) {
        return xpHistoryRepository.findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(user.getUserId(), Pageable.unpaged())
            .getContent();
    }
}
