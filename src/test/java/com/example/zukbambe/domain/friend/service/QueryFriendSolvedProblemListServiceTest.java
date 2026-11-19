package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.Friendship;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.presentation.dto.response.FriendSolvedProblemListResponse;
import com.example.zukbambe.domain.friend.presentation.dto.response.FriendSolvedProblemResponse;
import com.example.zukbambe.domain.friend.presentation.dto.response.SolvedFriendResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// 매 테스트마다 새 유저를 만들어 친구를 맺으므로, 다른 테스트 데이터는 결과에 섞이지 않는다
@SpringBootTest
class QueryFriendSolvedProblemListServiceTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 18, 12, 0);

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryFriendSolvedProblemListService queryFriendSolvedProblemListService;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRankRepository userRankRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User me;

    @BeforeEach
    void setUp() {
        me = newUser("me");
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(me.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 친구가_푼_문제를_친구가_최근에_푼_순서로_보여준다() {
        User friend = newFriend(me);
        Problem old = newProblem();
        Problem recent = newProblem();
        solve(friend, old, BASE);
        solve(friend, recent, BASE.plusHours(1));

        assertThat(query().problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(recent.getProblemId(), old.getProblemId());
    }

    @Test
    void 내가_이미_푼_문제는_빠진다() {
        User friend = newFriend(me);
        Problem solvedByMe = newProblem();
        Problem notSolvedByMe = newProblem();
        solve(friend, solvedByMe, BASE);
        solve(friend, notSolvedByMe, BASE);
        solve(me, solvedByMe, BASE.minusDays(1));

        assertThat(query().problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(notSolvedByMe.getProblemId());
    }

    @Test
    void 여러_친구가_푼_문제는_한_번만_나오고_푼_친구들은_최근에_푼_순서로_나온다() {
        User first = newFriend(me);
        User second = newFriend(me);
        Problem problem = newProblem();
        solve(first, problem, BASE);
        solve(second, problem, BASE.plusMinutes(30));

        List<FriendSolvedProblemResponse> problems = query().problems();

        assertThat(problems).hasSize(1);
        assertThat(problems.getFirst().friends()).extracting(SolvedFriendResponse::userId)
            .containsExactly(second.getUserId(), first.getUserId());
    }

    @Test
    void 정렬은_그_문제를_가장_최근에_푼_친구_기준이다() {
        User first = newFriend(me);
        User second = newFriend(me);
        Problem oldThenRecent = newProblem();
        Problem middle = newProblem();
        solve(first, oldThenRecent, BASE);
        solve(first, middle, BASE.plusHours(1));
        solve(second, oldThenRecent, BASE.plusHours(2));

        assertThat(query().problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(oldThenRecent.getProblemId(), middle.getProblemId());
    }

    @Test
    void 내가_보낸_요청이든_받은_요청이든_수락된_친구면_나온다() {
        User iRequested = newUser("friend");
        User theyRequested = newUser("friend");
        accept(Friendship.of(me, iRequested));
        accept(Friendship.of(theyRequested, me));
        Problem first = newProblem();
        Problem second = newProblem();
        solve(iRequested, first, BASE);
        solve(theyRequested, second, BASE.plusMinutes(1));

        assertThat(query().problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(second.getProblemId(), first.getProblemId());
    }

    @Test
    void 친구가_아니거나_수락_전인_유저가_푼_문제는_안_나온다() {
        User stranger = newUser("stranger");
        User pending = newUser("pending");
        friendshipRepository.save(Friendship.of(me, pending));
        solve(stranger, newProblem(), BASE);
        solve(pending, newProblem(), BASE);

        FriendSolvedProblemListResponse response = query();

        assertThat(response.problems()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void 문제와_친구_정보를_함께_내려준다() {
        User friend = newFriend(me);
        Problem problem = newProblem();
        solve(friend, problem, BASE);

        FriendSolvedProblemResponse response = query().problems().getFirst();

        assertThat(response.title()).isEqualTo(problem.getTitle());
        assertThat(response.rank().name()).isEqualTo("이슬");
        SolvedFriendResponse solvedFriend = response.friends().getFirst();
        assertThat(solvedFriend.name()).isEqualTo(friend.getName());
        assertThat(solvedFriend.rankName()).isEqualTo("죽순");
        assertThat(solvedFriend.solvedAt()).isEqualTo(BASE);
    }

    @Test
    void 문제_단위로_페이지를_나눈다() {
        User first = newFriend(me);
        User second = newFriend(me);
        Problem p1 = newProblem();
        Problem p2 = newProblem();
        Problem p3 = newProblem();
        solve(first, p1, BASE.plusHours(3));
        solve(second, p1, BASE.plusHours(2));
        solve(first, p2, BASE.plusHours(1));
        solve(second, p3, BASE);

        FriendSolvedProblemListResponse page0 = queryFriendSolvedProblemListService.execute(0, 2);
        FriendSolvedProblemListResponse page1 = queryFriendSolvedProblemListService.execute(1, 2);

        assertThat(page0.problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(p1.getProblemId(), p2.getProblemId());
        assertThat(page1.problems()).extracting(FriendSolvedProblemResponse::problemId)
            .containsExactly(p3.getProblemId());
        assertThat(page0.totalElements()).isEqualTo(3);
        assertThat(page0.totalPages()).isEqualTo(2);
    }

    private FriendSolvedProblemListResponse query() {
        return queryFriendSolvedProblemListService.execute(0, 100);
    }

    private User newFriend(User user) {
        User friend = newUser("friend");
        accept(Friendship.of(user, friend));
        return friend;
    }

    private void accept(Friendship friendship) {
        friendship.accept(BASE.minusDays(7));
        friendshipRepository.save(friendship);
    }

    private User newUser(String prefix) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(User.builder()
            .email(prefix + "-" + unique + "@dsm.hs.kr")
            .password("password")
            .name(prefix + "-" + unique)
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

    private void solve(User user, Problem problem, LocalDateTime solvedTime) {
        jdbcTemplate.update("insert into tbl_solved_histories (user_id, problem_id, solved_time) values (?, ?, ?)",
            user.getUserId(), problem.getProblemId(), solvedTime);
    }
}
