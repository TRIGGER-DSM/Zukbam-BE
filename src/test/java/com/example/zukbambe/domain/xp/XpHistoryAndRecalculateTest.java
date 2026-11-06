package com.example.zukbambe.domain.xp;

import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.exception.UserNotFoundException;
import com.example.zukbambe.domain.xp.domain.XpHistory;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import com.example.zukbambe.domain.xp.presentation.dto.response.XpHistoryListResponse;
import com.example.zukbambe.domain.xp.presentation.dto.response.XpHistoryResponse;
import com.example.zukbambe.domain.xp.service.QueryXpHistoryListService;
import com.example.zukbambe.domain.xp.service.RecalculateMyXpService;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class XpHistoryAndRecalculateTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryXpHistoryListService queryXpHistoryListService;
    @Autowired
    private RecalculateMyXpService recalculateMyXpService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private XpHistoryRepository xpHistoryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
            .email(UUID.randomUUID().toString().substring(0, 8) + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.STUDENT)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(user.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 내역은_최신순으로_페이지네이션된다() {
        history(10, LocalDateTime.of(2026, 11, 1, 10, 0));
        history(30, LocalDateTime.of(2026, 11, 3, 10, 0));
        history(20, LocalDateTime.of(2026, 11, 2, 10, 0));

        XpHistoryListResponse page0 = queryXpHistoryListService.execute(user.getUserId(), 0, 2);
        XpHistoryListResponse page1 = queryXpHistoryListService.execute(user.getUserId(), 1, 2);

        assertThat(page0.xpHistories()).extracting(XpHistoryResponse::amount).containsExactly(30L, 20L);
        assertThat(page1.xpHistories()).extracting(XpHistoryResponse::amount).containsExactly(10L);
        assertThat(page0.totalElements()).isEqualTo(3);
        assertThat(page0.totalPages()).isEqualTo(2);
        assertThat(page0.xpHistories().getFirst().reason()).isEqualTo("reason");
    }

    @Test
    void 없는_유저의_내역을_조회하면_실패한다() {
        assertThatThrownBy(() -> queryXpHistoryListService.execute(-1L, 0, 20))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 재계산하면_내역_합계로_XP와_랭크를_맞춘다() {
        history(100, LocalDateTime.of(2026, 11, 1, 10, 0));
        history(20, LocalDateTime.of(2026, 11, 2, 10, 0));
        // 내역과 어긋난 상태를 만든다
        jdbcTemplate.update("update tbl_users set xp = 0 where user_id = ?", user.getUserId());

        recalculateMyXpService.execute();

        assertThat(xpOf()).isEqualTo(120L);
        assertThat(rankName()).isEqualTo("대나무 1m");
    }

    @Test
    void 내역이_없는_유저를_재계산하면_0과_죽순이_된다() {
        jdbcTemplate.update(
            "update tbl_users set xp = 700, user_rank_id = (select user_rank_id from tbl_user_ranks where name = '대나무 5m') where user_id = ?",
            user.getUserId());

        recalculateMyXpService.execute();

        assertThat(xpOf()).isZero();
        assertThat(rankName()).isEqualTo("죽순");
    }

    private void history(long amount, LocalDateTime createdAt) {
        xpHistoryRepository.save(XpHistory.of(user, amount, "reason", null, null, createdAt));
    }

    private long xpOf() {
        return userRepository.findById(user.getUserId()).orElseThrow().getXp();
    }

    private String rankName() {
        Long rankId = userRepository.findById(user.getUserId()).orElseThrow().getRank().getUserRankId();
        return userRankRepository.findById(rankId).orElseThrow().getName();
    }
}
