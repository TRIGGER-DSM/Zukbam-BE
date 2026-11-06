package com.example.zukbambe.domain.ranking;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.ranking.scheduler.NightlyRankingScheduler;
import com.example.zukbambe.domain.ranking.service.CreateRankingSnapshotService;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.xp.domain.XpHistory;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreateRankingSnapshotServiceTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    // 2026-11-04(수) 03:00. 이번 주는 11-02(월)부터, 이번 달은 11-01부터다
    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(LocalDateTime.of(2026, 11, 4, 3, 0).atZone(SEOUL).toInstant(), SEOUL);
        }
    }

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private CreateRankingSnapshotService createRankingSnapshotService;
    @Autowired
    private NightlyRankingScheduler nightlyRankingScheduler;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        user = newUser();
    }

    @Test
    void 주간_XP는_이번_주_월요일_0시부터만_센다() {
        history(user, 10, LocalDateTime.of(2026, 11, 1, 23, 59, 59));
        history(user, 20, LocalDateTime.of(2026, 11, 2, 0, 0, 0));

        createRankingSnapshotService.execute();

        assertThat(snapshotOf("WEEKLY", user).get("xp")).isEqualTo(20L);
        assertThat(snapshotOf("MONTHLY", user).get("xp")).isEqualTo(30L);
    }

    @Test
    void 기간_안에_처음_푼_문제_수를_센다() {
        solvedAt(user, LocalDateTime.of(2026, 11, 1, 23, 59, 59));
        solvedAt(user, LocalDateTime.of(2026, 11, 2, 0, 0, 0));
        solvedAt(user, LocalDateTime.of(2026, 10, 31, 12, 0, 0));

        createRankingSnapshotService.execute();

        assertThat(snapshotOf("WEEKLY", user).get("solved_count")).isEqualTo(1L);
        assertThat(snapshotOf("MONTHLY", user).get("solved_count")).isEqualTo(2L);
        assertThat(snapshotOf("ALL", user).get("solved_count")).isEqualTo(3L);
    }

    @Test
    void 활동이_없는_유저도_0으로_들어간다() {
        createRankingSnapshotService.execute();

        Map<String, Object> weekly = snapshotOf("WEEKLY", user);
        assertThat(weekly.get("xp")).isEqualTo(0L);
        assertThat(weekly.get("solved_count")).isEqualTo(0L);
        assertThat(weekly.get("streak")).isEqualTo(0);
    }

    @Test
    void 끊긴_스트릭은_0으로_이어지는_스트릭은_그대로_들어간다() {
        User alive = newUser();
        streak(user, 3, LocalDate.of(2026, 11, 2));
        streak(alive, 2, LocalDate.of(2026, 11, 3));

        createRankingSnapshotService.execute();

        assertThat(snapshotOf("ALL", user).get("streak")).isEqualTo(0);
        assertThat(snapshotOf("ALL", alive).get("streak")).isEqualTo(2);
        assertThat(snapshotOf("WEEKLY", alive).get("streak")).isEqualTo(2);
    }

    @Test
    void 배치는_XP를_재계산한_뒤_스냅샷을_만든다() {
        history(user, 100, LocalDateTime.of(2026, 11, 3, 12, 0));
        jdbcTemplate.update("update tbl_users set xp = 0 where user_id = ?", user.getUserId());

        nightlyRankingScheduler.run();

        Map<String, Object> all = snapshotOf("ALL", user);
        assertThat(all.get("xp")).isEqualTo(100L);
        assertThat(all.get("user_rank_id")).isEqualTo(userRankRepository.findByName("대나무 1m").orElseThrow().getUserRankId());
    }

    private User newUser() {
        return userRepository.save(User.builder()
            .email(UUID.randomUUID().toString().substring(0, 8) + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
    }

    private void history(User target, long amount, LocalDateTime createdAt) {
        xpHistoryRepository.save(XpHistory.of(target, amount, "reason", null, null, createdAt));
    }

    private void solvedAt(User target, LocalDateTime solvedTime) {
        Problem problem = problemRepository.save(Problem.builder()
            .title("problem-" + UUID.randomUUID())
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName("이슬").orElseThrow())
            .build());
        jdbcTemplate.update("insert into tbl_solved_histories (user_id, problem_id, solved_time) values (?, ?, ?)",
            target.getUserId(), problem.getProblemId(), solvedTime);
    }

    private void streak(User target, int current, LocalDate lastSolvedDate) {
        jdbcTemplate.update(
            "insert into tbl_streaks (user_id, current_streak, max_streak, last_solved_date) values (?, ?, ?, ?)",
            target.getUserId(), current, current, lastSolvedDate);
    }

    private Map<String, Object> snapshotOf(String period, User target) {
        return jdbcTemplate.queryForMap(
            "select xp, solved_count, streak, user_rank_id from tbl_ranking_snapshots where period = ? and user_id = ?",
            period, target.getUserId());
    }
}
