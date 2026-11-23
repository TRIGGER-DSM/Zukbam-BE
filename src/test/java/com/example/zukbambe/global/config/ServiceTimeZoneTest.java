package com.example.zukbambe.global.config;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.quest.facade.QuestFacade;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.service.ApplyGradingResultService;
import com.example.zukbambe.domain.streak.presentation.dto.response.GrassDayResponse;
import com.example.zukbambe.domain.streak.service.QueryGrassService;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// 서버(JVM) 시간대가 UTC여도 저장되는 시각과 날짜 집계는 한국 시간 기준이어야 한다.
// 한국 시간 11-20 23:30은 UTC로 11-20 14:30이라, 시간대가 섞이면 시각이 9시간 어긋나거나 다음 날로 집계된다
@SpringBootTest
class ServiceTimeZoneTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 20, 23, 30);
    private static final LocalDate TODAY = NOW.toLocalDate();

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW.atZone(SEOUL).toInstant(), SEOUL);
        }
    }

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private ApplyGradingResultService applyGradingResultService;
    @Autowired
    private QueryGrassService queryGrassService;
    @Autowired
    private QuestFacade questFacade;

    private User user;
    private Solution solution;

    @BeforeEach
    void setUp() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        user = userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("user-" + unique)
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
        Problem problem = problemRepository.save(Problem.builder()
            .title("problem-" + unique)
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName("이슬").orElseThrow())
            .build());
        solution = solutionRepository.save(Solution.of(user, problem, Language.PYTHON, "print(1)"));
    }

    @Test
    void 제출_시각과_가입_시각은_한국_시간으로_저장된다() {
        assertThat(solutionRepository.findById(solution.getSolutionId()).orElseThrow().getSubmittedAt()).isEqualTo(NOW);
        assertThat(userRepository.findById(user.getUserId()).orElseThrow().getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void 밤늦게_푼_문제는_그날_잔디에_들어간다() {
        accept();

        assertThat(queryGrassService.execute(user.getUserId(), TODAY.minusDays(1), TODAY.plusDays(1)).days())
            .containsExactly(new GrassDayResponse(TODAY, 1));
    }

    @Test
    void 밤늦게_낸_제출과_푼_문제는_그날_퀘스트_활동으로_센다() {
        accept();

        DailyActivity today = questFacade.getActivity(user.getUserId(), TODAY);
        assertThat(today.submissionCount()).isEqualTo(1);
        assertThat(today.solvedCount()).isEqualTo(1);
        assertThat(questFacade.getActivity(user.getUserId(), TODAY.plusDays(1)).submissionCount()).isZero();
    }

    private void accept() {
        applyGradingResultService.execute(solution.getSolutionId(), new JudgeResult(JudgeStatus.ACCEPTED, 0.01, 1000L));
    }
}
