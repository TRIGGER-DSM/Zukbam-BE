package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemSolveStatus;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemSummaryResponse;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.zukbambe.domain.solution.domain.enums.Status.ACCEPTED;
import static com.example.zukbambe.domain.solution.domain.enums.Status.PENDING;
import static com.example.zukbambe.domain.solution.domain.enums.Status.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

// 문제 번호순 목록의 마지막 문제들이 방금 만든 문제이므로, 한 문제씩 페이지를 잘라 읽는다
@SpringBootTest
class ProblemStatsTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryProblemListService queryProblemListService;
    @Autowired
    private QueryProblemDetailService queryProblemDetailService;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User me;

    @BeforeEach
    void setUp() {
        me = newUser();
        login(me);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 푼_사람_수와_처음_정답을_받기까지의_평균_시도를_센다() {
        Problem problem = newProblem();
        User threeTries = newUser();
        User oneTry = newUser();
        User neverSolved = newUser();
        submit(threeTries, problem, WRONG_ANSWER);
        submit(threeTries, problem, WRONG_ANSWER);
        submit(threeTries, problem, ACCEPTED);
        solved(threeTries, problem);
        submit(oneTry, problem, ACCEPTED);
        solved(oneTry, problem);
        // 못 푼 사람의 제출이 평균에 섞이면 값이 달라지도록 제출 수를 다르게 둔다
        for (int i = 0; i < 4; i++) {
            submit(neverSolved, problem, WRONG_ANSWER);
        }

        ProblemSummaryResponse summary = lastProblems(1).getFirst();

        assertThat(summary.solverCount()).isEqualTo(2);
        assertThat(summary.averageAttempts()).isEqualTo(2.0);
    }

    @Test
    void 정답을_받은_뒤의_제출은_평균_시도에_들어가지_않는다() {
        Problem problem = newProblem();
        User user = newUser();
        submit(user, problem, WRONG_ANSWER);
        submit(user, problem, ACCEPTED);
        submit(user, problem, WRONG_ANSWER);
        submit(user, problem, ACCEPTED);
        solved(user, problem);

        assertThat(lastProblems(1).getFirst().averageAttempts()).isEqualTo(2.0);
    }

    @Test
    void 푼_사람이_없으면_0명이고_평균_시도는_null이다() {
        Problem problem = newProblem();
        submit(newUser(), problem, WRONG_ANSWER);

        ProblemSummaryResponse summary = lastProblems(1).getFirst();

        assertThat(summary.solverCount()).isZero();
        assertThat(summary.averageAttempts()).isNull();
    }

    @Test
    void 상세에도_목록과_같은_푼_사람_수와_평균_시도가_나온다() {
        Problem problem = newProblem();
        User threeTries = newUser();
        User oneTry = newUser();
        User neverSolved = newUser();
        submit(threeTries, problem, WRONG_ANSWER);
        submit(threeTries, problem, WRONG_ANSWER);
        submit(threeTries, problem, ACCEPTED);
        solved(threeTries, problem);
        submit(oneTry, problem, ACCEPTED);
        solved(oneTry, problem);
        submit(neverSolved, problem, WRONG_ANSWER);

        ProblemDetailResponse detail = queryProblemDetailService.execute(problem.getProblemId());
        ProblemSummaryResponse summary = lastProblems(1).getFirst();

        assertThat(detail.solverCount()).isEqualTo(2);
        assertThat(detail.averageAttempts()).isEqualTo(2.0);
        assertThat(detail.solverCount()).isEqualTo(summary.solverCount());
        assertThat(detail.averageAttempts()).isEqualTo(summary.averageAttempts());
    }

    @Test
    void 상세에서_푼_사람이_없으면_0명이고_평균_시도는_null이다() {
        Problem problem = newProblem();
        submit(newUser(), problem, WRONG_ANSWER);

        ProblemDetailResponse detail = queryProblemDetailService.execute(problem.getProblemId());

        assertThat(detail.solverCount()).isZero();
        assertThat(detail.averageAttempts()).isNull();
    }

    @Test
    void 푼_문제는_SOLVED_제출만_했으면_ATTEMPTED_안_했으면_UNATTEMPTED다() {
        User other = newUser();
        Problem solvedByMe = newProblem();
        Problem wrongByMe = newProblem();
        Problem pendingByMe = newProblem();
        Problem solvedOnlyByOther = newProblem();
        submit(me, solvedByMe, WRONG_ANSWER);
        submit(me, solvedByMe, ACCEPTED);
        solved(me, solvedByMe);
        submit(me, wrongByMe, WRONG_ANSWER);
        submit(me, pendingByMe, PENDING);
        submit(other, solvedOnlyByOther, ACCEPTED);
        solved(other, solvedOnlyByOther);

        List<ProblemSummaryResponse> summaries = lastProblems(4);

        assertThat(summaries).extracting(ProblemSummaryResponse::status).containsExactly(
            ProblemSolveStatus.SOLVED,
            ProblemSolveStatus.ATTEMPTED,
            ProblemSolveStatus.ATTEMPTED,
            ProblemSolveStatus.UNATTEMPTED
        );
    }

    @Test
    void 로그인하지_않으면_풀이_상태는_null이고_통계는_나온다() {
        Problem problem = newProblem();
        submit(me, problem, ACCEPTED);
        solved(me, problem);
        SecurityContextHolder.clearContext();

        ProblemSummaryResponse summary = lastProblems(1).getFirst();

        assertThat(summary.status()).isNull();
        assertThat(summary.solverCount()).isEqualTo(1);
    }

    // 문제 번호순이라 방금 만든 count개 문제가 마지막이다. 만든 순서대로 돌려준다
    private List<ProblemSummaryResponse> lastProblems(int count) {
        int total = (int) queryProblemListService.execute(0, 1).totalElements();
        List<ProblemSummaryResponse> summaries = new ArrayList<>();
        for (int i = count; i >= 1; i--) {
            summaries.add(queryProblemListService.execute(total - i, 1).problems().getFirst());
        }
        return summaries;
    }

    private void login(User user) {
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(user.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    private User newUser() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(User.builder()
            .email("user-" + unique + "@dsm.hs.kr")
            .password("password")
            .name("user-" + unique)
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
    }

    private Problem newProblem() {
        return problemRepository.save(Problem.builder()
            .title("problem-" + UUID.randomUUID())
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName("이슬").orElseThrow())
            .build());
    }

    private void submit(User user, Problem problem, Status status) {
        Solution solution = Solution.of(user, problem, Language.PYTHON, "code");
        if (status != PENDING) {
            solution.grade(status, 0.1, 1024L);
        }
        solutionRepository.save(solution);
    }

    private void solved(User user, Problem problem) {
        jdbcTemplate.update("insert into tbl_solved_histories (user_id, problem_id, solved_time) values (?, ?, ?)",
            user.getUserId(), problem.getProblemId(), LocalDateTime.now());
    }
}
