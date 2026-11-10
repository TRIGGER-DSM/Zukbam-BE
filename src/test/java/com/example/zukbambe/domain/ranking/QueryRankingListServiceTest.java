package com.example.zukbambe.domain.ranking;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingResponse;
import com.example.zukbambe.domain.ranking.service.QueryRankingListService;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.xp.domain.XpHistory;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import com.example.zukbambe.domain.xp.facade.XpFacade;
import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.AfterEach;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.SOLVED_COUNT;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.STREAK;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.XP;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod.ALL;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod.MONTHLY;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod.WEEKLY;
import static org.assertj.core.api.Assertions.assertThat;

// 다른 테스트의 유저도 같은 DB에 있으므로, 순위를 직접 확인하는 테스트는 아주 큰 XP를 줘서 맨 위로 올린다
@SpringBootTest
class QueryRankingListServiceTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final long TOP = 1_000_000_000_000L;

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
    private QueryRankingListService queryRankingListService;
    @Autowired
    private XpFacade xpFacade;
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

    private final List<User> created = new ArrayList<>();

    // 다음 테스트에서 맨 위를 차지하지 않도록 큰 XP를 되돌린다
    @AfterEach
    void tearDown() {
        created.forEach(user -> jdbcTemplate.update("update tbl_users set xp = 0 where user_id = ?", user.getUserId()));
    }

    @Test
    void XP를_얻으면_랭킹에_바로_반영된다() {
        User user = newUser(Role.STUDENT, TOP);

        xpFacade.grantForSolve(user, newProblem());

        assertThat(rowOf(query(XP, ALL, false), user).xp()).isEqualTo(TOP + 10);
        assertThat(rowOf(query(XP, WEEKLY, false), user).xp()).isEqualTo(10L);
    }

    @Test
    void 주간_XP는_이번_주_월요일_0시부터만_센다() {
        User user = newUser(Role.STUDENT, 0);
        history(user, 10, LocalDateTime.of(2026, 11, 1, 23, 59, 59));
        history(user, 20, LocalDateTime.of(2026, 11, 2, 0, 0, 0));

        assertThat(rowOf(query(XP, WEEKLY, false), user).xp()).isEqualTo(20L);
        assertThat(rowOf(query(XP, MONTHLY, false), user).xp()).isEqualTo(30L);
    }

    @Test
    void 기간_안에_처음_푼_문제_수를_센다() {
        User user = newUser(Role.STUDENT, 0);
        solvedAt(user, LocalDateTime.of(2026, 11, 1, 23, 59, 59));
        solvedAt(user, LocalDateTime.of(2026, 11, 2, 0, 0, 0));
        solvedAt(user, LocalDateTime.of(2026, 10, 31, 12, 0, 0));

        assertThat(rowOf(query(SOLVED_COUNT, WEEKLY, false), user).solvedCount()).isEqualTo(1L);
        assertThat(rowOf(query(SOLVED_COUNT, MONTHLY, false), user).solvedCount()).isEqualTo(2L);
        assertThat(rowOf(query(SOLVED_COUNT, ALL, false), user).solvedCount()).isEqualTo(3L);
    }

    @Test
    void 활동이_없는_유저도_0으로_나온다() {
        User user = newUser(Role.STUDENT, 0);

        RankingResponse row = rowOf(query(XP, WEEKLY, false), user);

        assertThat(row.xp()).isEqualTo(0L);
        assertThat(row.solvedCount()).isEqualTo(0L);
        assertThat(row.streak()).isEqualTo(0);
        assertThat(row.rankName()).isEqualTo("죽순");
    }

    @Test
    void 끊긴_스트릭은_0으로_이어지는_스트릭은_그대로_나온다() {
        User broken = newUser(Role.STUDENT, 0);
        User alive = newUser(Role.STUDENT, 0);
        streak(broken, 3, LocalDate.of(2026, 11, 2));
        streak(alive, 2, LocalDate.of(2026, 11, 3));

        RankingListResponse response = query(STREAK, ALL, false);

        assertThat(rowOf(response, broken).streak()).isEqualTo(0);
        assertThat(rowOf(response, alive).streak()).isEqualTo(2);
    }

    @Test
    void XP가_같으면_id가_작은_유저가_앞선다() {
        User tieA = newUser(Role.STUDENT, TOP);
        User high = newUser(Role.STUDENT, TOP * 3);
        User tieB = newUser(Role.STUDENT, TOP);

        List<RankingResponse> rows = query(XP, ALL, false).rankings().subList(0, 3);

        assertThat(rows).extracting(RankingResponse::userId)
            .containsExactly(high.getUserId(), tieA.getUserId(), tieB.getUserId());
        assertThat(rows).extracting(RankingResponse::position).containsExactly(1L, 2L, 3L);
        assertThat(rows.getFirst().name()).isEqualTo(high.getName());
    }

    @Test
    void 선생님을_빼면_순위가_빈틈없이_다시_매겨진다() {
        User teacher = newUser(Role.TEACHER, TOP * 5);
        User first = newUser(Role.STUDENT, TOP * 3);
        User second = newUser(Role.STUDENT, TOP);

        RankingListResponse withTeachers = query(XP, ALL, false);
        RankingListResponse withoutTeachers = query(XP, ALL, true);

        assertThat(withTeachers.rankings().getFirst().userId()).isEqualTo(teacher.getUserId());
        assertThat(withoutTeachers.rankings().subList(0, 2)).extracting(RankingResponse::userId)
            .containsExactly(first.getUserId(), second.getUserId());
        assertThat(withoutTeachers.rankings().subList(0, 2)).extracting(RankingResponse::position)
            .containsExactly(1L, 2L);
        assertThat(withoutTeachers.totalElements())
            .isEqualTo(jdbcTemplate.queryForObject("select count(*) from tbl_users where role <> 'TEACHER'", Long.class));
    }

    @Test
    void 푼_문제_수_기준으로_정렬한다() {
        User manyXp = newUser(Role.STUDENT, TOP);
        User manySolved = newUser(Role.STUDENT, 0);
        solvedAt(manyXp, LocalDateTime.of(2026, 11, 3, 12, 0));
        solvedAt(manySolved, LocalDateTime.of(2026, 11, 3, 12, 0));
        solvedAt(manySolved, LocalDateTime.of(2026, 11, 3, 13, 0));

        List<Long> order = idsOf(query(SOLVED_COUNT, WEEKLY, false), manyXp, manySolved);

        assertThat(order).containsExactly(manySolved.getUserId(), manyXp.getUserId());
    }

    @Test
    void 스트릭은_기간을_줘도_전체와_같다() {
        User longStreak = newUser(Role.STUDENT, 0);
        User shortStreak = newUser(Role.STUDENT, 0);
        streak(shortStreak, 2, LocalDate.of(2026, 11, 3));
        streak(longStreak, 9, LocalDate.of(2026, 11, 3));

        RankingListResponse weekly = query(STREAK, WEEKLY, false);

        assertThat(weekly).isEqualTo(query(STREAK, ALL, false));
        assertThat(idsOf(weekly, longStreak, shortStreak)).containsExactly(longStreak.getUserId(), shortStreak.getUserId());
    }

    @Test
    void 페이지를_나눠도_순위는_이어진다() {
        for (long i = 5; i >= 1; i--) {
            newUser(Role.STUDENT, TOP * i);
        }

        RankingListResponse page1 = queryRankingListService.execute(XP, ALL, false, 1, 2);

        assertThat(page1.rankings()).extracting(RankingResponse::position).containsExactly(3L, 4L);
        assertThat(page1.rankings()).extracting(RankingResponse::xp).containsExactly(TOP * 3, TOP * 2);
        assertThat(page1.totalElements()).isEqualTo(jdbcTemplate.queryForObject("select count(*) from tbl_users", Long.class));
    }

    private RankingListResponse query(RankingMetric metric, RankingPeriod period, boolean excludeTeachers) {
        return queryRankingListService.execute(metric, period, excludeTeachers, 0, 10_000);
    }

    private RankingResponse rowOf(RankingListResponse response, User user) {
        return response.rankings().stream()
            .filter(row -> row.userId().equals(user.getUserId()))
            .findFirst()
            .orElseThrow();
    }

    // 다른 테스트 유저는 빼고 주어진 유저들의 순서만 본다
    private List<Long> idsOf(RankingListResponse response, User... users) {
        List<Long> ids = Arrays.stream(users).map(User::getUserId).toList();
        return response.rankings().stream().map(RankingResponse::userId).filter(ids::contains).toList();
    }

    private User newUser(Role role, long xp) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        User user = userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("user-" + unique)
            .role(role)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
        jdbcTemplate.update("update tbl_users set xp = ? where user_id = ?", xp, user.getUserId());
        created.add(user);
        return user;
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

    private void history(User target, long amount, LocalDateTime createdAt) {
        xpHistoryRepository.save(XpHistory.of(target, amount, "reason", null, null, createdAt));
    }

    private void solvedAt(User target, LocalDateTime solvedTime) {
        jdbcTemplate.update("insert into tbl_solved_histories (user_id, problem_id, solved_time) values (?, ?, ?)",
            target.getUserId(), newProblem().getProblemId(), solvedTime);
    }

    private void streak(User target, int current, LocalDate lastSolvedDate) {
        jdbcTemplate.update(
            "insert into tbl_streaks (user_id, current_streak, max_streak, last_solved_date) values (?, ?, ?, ?)",
            target.getUserId(), current, current, lastSolvedDate);
    }
}
