package com.example.zukbambe.global.config;

import com.example.zukbambe.ZukbamBeApplication;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.service.GradeSolutionService;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class GradingGracefulShutdownTest {

    private static final String SLOW_CODE = "slow code";
    private static final long JUDGE_MILLIS = 3000;

    @Test
    void 종료할_때_진행_중인_채점을_끝까지_반영한다() throws Exception {
        CountDownLatch judgeStarted = new CountDownLatch(1);
        JudgeProvider slowJudgeProvider = request -> {
            // 기동 시 복구되는 다른 PENDING 풀이는 바로 채점한다
            if (SLOW_CODE.equals(request.code())) {
                judgeStarted.countDown();
                sleep(JUDGE_MILLIS);
            }
            return new JudgeResult(JudgeStatus.ACCEPTED, 0.01, 1000L);
        };

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ZukbamBeApplication.class)
            .properties("server.port=0")
            .initializers(applicationContext -> ((GenericApplicationContext) applicationContext).registerBean(
                "slowJudgeProvider", JudgeProvider.class, () -> slowJudgeProvider, definition -> definition.setPrimary(true)))
            .run();

        Long solutionId;
        Environment environment = context.getEnvironment();
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        try {
            solutionId = savePendingSolution(context);
            context.getBean(GradeSolutionService.class).execute(solutionId);
            assertThat(judgeStarted.await(10, TimeUnit.SECONDS)).isTrue();
        } finally {
            context.close();
        }

        assertThat(findStatus(url, username, password, solutionId)).isEqualTo("ACCEPTED");
    }

    private Long savePendingSolution(ConfigurableApplicationContext context) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        UserRank rank = context.getBean(UserRankRepository.class)
            .save(UserRank.builder().name("rank-" + unique).xp(0).build());
        User user = context.getBean(UserRepository.class).save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(rank)
            .build());
        Problem problem = context.getBean(ProblemRepository.class).save(Problem.builder()
            .title("problem-" + unique)
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(context.getBean(ProblemRankRepository.class).findByName("이슬").orElseThrow())
            .build());
        context.getBean(TestCaseRepository.class).save(TestCase.of(problem, "1 2", "3", true));
        return context.getBean(SolutionRepository.class)
            .save(Solution.of(user, problem, Language.PYTHON, SLOW_CODE))
            .getSolutionId();
    }

    private String findStatus(String url, String username, String password, Long solutionId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                 "select status from tbl_solutions where solution_id = ?")) {
            statement.setLong(1, solutionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertThat(resultSet.next()).isTrue();
                return resultSet.getString(1);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
