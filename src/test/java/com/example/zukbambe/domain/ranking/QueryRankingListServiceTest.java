package com.example.zukbambe.domain.ranking;

import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.ranking.domain.RankingSnapshot;
import com.example.zukbambe.domain.ranking.domain.enums.RankingMetric;
import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import com.example.zukbambe.domain.ranking.domain.repository.RankingSnapshotRepository;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingListResponse;
import com.example.zukbambe.domain.ranking.presentation.dto.response.RankingResponse;
import com.example.zukbambe.domain.ranking.service.QueryRankingListService;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.SOLVED_COUNT;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.STREAK;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingMetric.XP;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod.ALL;
import static com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod.WEEKLY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueryRankingListServiceTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryRankingListService queryRankingListService;
    @Autowired
    private RankingSnapshotRepository rankingSnapshotRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;

    private UserRank sprout;

    // 다른 테스트가 만든 스냅샷과 섞이지 않도록 비우고 시작한다
    @BeforeEach
    void setUp() {
        rankingSnapshotRepository.deleteAllInBatch();
        sprout = userRankRepository.findByName("죽순").orElseThrow();
    }

    @Test
    void XP가_같으면_id가_작은_유저가_앞선다() {
        User high = newUser(Role.STUDENT);
        User tieA = newUser(Role.STUDENT);
        User tieB = newUser(Role.STUDENT);
        snapshot(ALL, tieB, 100, 0, 0);
        snapshot(ALL, high, 300, 0, 0);
        snapshot(ALL, tieA, 100, 0, 0);

        List<RankingResponse> rows = query(XP, ALL, false).rankings();

        assertThat(rows).extracting(RankingResponse::userId)
            .containsExactly(high.getUserId(), tieA.getUserId(), tieB.getUserId());
        assertThat(rows).extracting(RankingResponse::position).containsExactly(1L, 2L, 3L);
        assertThat(rows.getFirst().name()).isEqualTo(high.getName());
        assertThat(rows.getFirst().rankName()).isEqualTo("죽순");
    }

    @Test
    void 선생님을_빼면_순위가_빈틈없이_다시_매겨진다() {
        User teacher = newUser(Role.TEACHER);
        User first = newUser(Role.STUDENT);
        User second = newUser(Role.STUDENT);
        snapshot(ALL, teacher, 500, 0, 0);
        snapshot(ALL, first, 300, 0, 0);
        snapshot(ALL, second, 100, 0, 0);

        RankingListResponse withTeachers = query(XP, ALL, false);
        RankingListResponse withoutTeachers = query(XP, ALL, true);

        assertThat(withTeachers.rankings()).extracting(RankingResponse::userId).first().isEqualTo(teacher.getUserId());
        assertThat(withoutTeachers.rankings()).extracting(RankingResponse::position).containsExactly(1L, 2L);
        assertThat(withoutTeachers.rankings()).extracting(RankingResponse::userId)
            .containsExactly(first.getUserId(), second.getUserId());
        assertThat(withoutTeachers.totalElements()).isEqualTo(2);
    }

    @Test
    void 푼_문제_수_기준으로_정렬한다() {
        User manyXp = newUser(Role.STUDENT);
        User manySolved = newUser(Role.STUDENT);
        snapshot(WEEKLY, manyXp, 500, 1, 0);
        snapshot(WEEKLY, manySolved, 10, 5, 0);

        List<RankingResponse> rows = query(SOLVED_COUNT, WEEKLY, false).rankings();

        assertThat(rows).extracting(RankingResponse::userId).containsExactly(manySolved.getUserId(), manyXp.getUserId());
        assertThat(rows.getFirst().solvedCount()).isEqualTo(5L);
        assertThat(rows.getFirst().xp()).isEqualTo(10L);
    }

    @Test
    void 스트릭은_기간을_줘도_전체와_같다() {
        User longStreak = newUser(Role.STUDENT);
        User shortStreak = newUser(Role.STUDENT);
        snapshot(ALL, shortStreak, 0, 0, 2);
        snapshot(ALL, longStreak, 0, 0, 9);
        // 주간 스냅샷은 순서가 반대지만 스트릭은 전체 기간만 본다
        snapshot(WEEKLY, shortStreak, 0, 0, 20);
        snapshot(WEEKLY, longStreak, 0, 0, 1);

        RankingListResponse weekly = query(STREAK, WEEKLY, false);

        assertThat(weekly).isEqualTo(query(STREAK, ALL, false));
        assertThat(weekly.rankings()).extracting(RankingResponse::userId)
            .containsExactly(longStreak.getUserId(), shortStreak.getUserId());
    }

    @Test
    void 페이지를_나눠도_순위는_이어진다() {
        for (int xp = 50; xp >= 10; xp -= 10) {
            snapshot(ALL, newUser(Role.STUDENT), xp, 0, 0);
        }

        RankingListResponse page1 = queryRankingListService.execute(XP, ALL, false, 1, 2);

        assertThat(page1.rankings()).extracting(RankingResponse::position).containsExactly(3L, 4L);
        assertThat(page1.rankings()).extracting(RankingResponse::xp).containsExactly(30L, 20L);
        assertThat(page1.totalElements()).isEqualTo(5);
        assertThat(page1.totalPages()).isEqualTo(3);
    }

    private RankingListResponse query(RankingMetric metric, RankingPeriod period, boolean excludeTeachers) {
        return queryRankingListService.execute(metric, period, excludeTeachers, 0, 20);
    }

    private User newUser(Role role) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(User.builder()
            .email(unique + "@dsm.hs.kr")
            .password("password")
            .name("user-" + unique)
            .role(role)
            .rank(sprout)
            .build());
    }

    private void snapshot(RankingPeriod period, User user, long xp, long solvedCount, int streak) {
        rankingSnapshotRepository.save(RankingSnapshot.builder()
            .period(period)
            .userId(user.getUserId())
            .userRankId(sprout.getUserRankId())
            .xp(xp)
            .solvedCount(solvedCount)
            .streak(streak)
            .snapshotAt(LocalDateTime.of(2026, 11, 4, 3, 0))
            .build());
    }
}
