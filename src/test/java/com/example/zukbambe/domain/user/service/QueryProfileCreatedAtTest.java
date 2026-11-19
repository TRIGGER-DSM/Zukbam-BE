package com.example.zukbambe.domain.user.service;

import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueryProfileCreatedAtTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryMyProfileService queryMyProfileService;
    @Autowired
    private QueryUserProfileService queryUserProfileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;

    private User user;

    @BeforeEach
    void setUp() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        UserRank rank = userRankRepository.save(UserRank.builder().name("rank-" + unique).xp(0).build());
        user = userRepository.save(User.builder()
            .email("user-" + unique + "@dsm.hs.kr")
            .password("password")
            .name("tester")
            .role(Role.TEACHER)
            .rank(rank)
            .build());

        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(user.getUserId(), Role.TEACHER), null, List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // DB는 마이크로초로 반올림해서 저장하므로 저장된 값을 다시 읽어 비교한다
    private LocalDateTime savedCreatedAt() {
        return userRepository.findById(user.getUserId()).orElseThrow().getCreatedAt();
    }

    @Test
    void 내_프로필에_가입일이_나온다() {
        assertThat(queryMyProfileService.execute().createdAt()).isEqualTo(savedCreatedAt());
    }

    @Test
    void 유저_프로필에_가입일이_나온다() {
        assertThat(queryUserProfileService.execute(user.getUserId()).createdAt()).isEqualTo(savedCreatedAt());
    }
}
