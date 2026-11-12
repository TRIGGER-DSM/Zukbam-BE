package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionResponse;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerySolutionCodeVisibilityTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QuerySolutionDetailService querySolutionDetailService;
    @Autowired
    private QuerySolutionListService querySolutionListService;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private SolvedHistoryRepository solvedHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private User viewer;
    private User other;
    private Problem problem;
    private Solution otherSolution;

    @BeforeEach
    void setUp() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        UserRank rank = userRankRepository.save(UserRank.builder().name("rank-" + unique).xp(0).build());
        viewer = saveUser("viewer-" + unique, rank);
        other = saveUser("other-" + unique, rank);
        problem = problemRepository.save(Problem.builder()
            .title("problem-" + unique)
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName("이슬").orElseThrow())
            .build());
        otherSolution = solutionRepository.save(Solution.of(other, problem, Language.PYTHON, "other code"));

        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(viewer.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 내_풀이는_문제를_못_풀었어도_코드를_볼_수_있다() {
        Solution mine = solutionRepository.save(Solution.of(viewer, problem, Language.PYTHON, "my code"));

        assertThat(querySolutionDetailService.execute(mine.getSolutionId()).code()).isEqualTo("my code");
    }

    @Test
    void 문제를_못_푼_유저는_다른_사람_코드를_볼_수_없다() {
        assertThat(querySolutionDetailService.execute(otherSolution.getSolutionId()).code()).isNull();
        assertThat(listCode()).isNull();
    }

    @Test
    void 문제를_푼_유저는_다른_사람_코드를_볼_수_있다() {
        transactionTemplate.executeWithoutResult(status ->
            solvedHistoryRepository.insertIfAbsent(viewer.getUserId(), problem.getProblemId(), LocalDateTime.now()));

        assertThat(querySolutionDetailService.execute(otherSolution.getSolutionId()).code()).isEqualTo("other code");
        assertThat(listCode()).isEqualTo("other code");
    }

    @Test
    void 선생님은_문제를_안_풀었어도_다른_사람_코드를_볼_수_있다() {
        User teacher = userRepository.save(User.builder()
            .email("teacher-" + UUID.randomUUID().toString().substring(0, 8) + "@dsm.hs.kr")
            .password("password")
            .name("teacher")
            .role(Role.TEACHER)
            .rank(viewer.getRank())
            .build());
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(teacher.getUserId(), Role.TEACHER), null, List.of()
        ));

        assertThat(querySolutionDetailService.execute(otherSolution.getSolutionId()).code()).isEqualTo("other code");
        assertThat(listCode()).isEqualTo("other code");
    }

    private String listCode() {
        return querySolutionListService.execute(1, 20, problem.getProblemId(), other.getEmail()).solutions().stream()
            .filter(response -> response.solutionId().equals(otherSolution.getSolutionId()))
            .findFirst()
            .orElseThrow()
            .code();
    }

    private User saveUser(String name, UserRank rank) {
        return userRepository.save(User.builder()
            .email(name + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(rank)
            .build());
    }
}
