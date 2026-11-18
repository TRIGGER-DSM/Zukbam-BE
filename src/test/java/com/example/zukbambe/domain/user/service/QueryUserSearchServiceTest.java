package com.example.zukbambe.domain.user.service;

import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.student.domain.Student;
import com.example.zukbambe.domain.student.domain.repository.StudentRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.presentation.dto.response.UserSearchListResponse;
import com.example.zukbambe.domain.user.presentation.dto.response.UserSearchResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// 다른 테스트의 유저도 같은 DB에 있으므로, 이름에 테스트마다 다른 토큰을 붙여서 내 유저만 골라 본다
@SpringBootTest
class QueryUserSearchServiceTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private QueryUserSearchService queryUserSearchService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRankRepository userRankRepository;

    private final List<User> created = new ArrayList<>();
    private String token;
    private User me;

    @BeforeEach
    void setUp() {
        token = UUID.randomUUID().toString().substring(0, 8);
        me = newUser("me-" + token, Role.STUDENT);
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
            new CustomUserDetails(me.getUserId(), Role.STUDENT), null, List.of()
        ));
    }

    // 고정 학번으로 찾는 테스트가 반복 실행으로 쌓인 데이터에 밀리지 않도록 만든 데이터를 지운다
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        studentRepository.deleteAll(studentRepository.findAllById(created.stream().map(User::getUserId).toList()));
        userRepository.deleteAll(created);
    }

    @Test
    void 이름의_퍼센트와_밑줄은_글자_그대로_찾는다() {
        newUser(token + "가", Role.STUDENT);

        assertThat(queryUserSearchService.execute("%", 0, 20).users()).isEmpty();
        assertThat(queryUserSearchService.execute("_", 0, 20).users()).isEmpty();
    }

    @Test
    void 학번_검색도_페이지를_나눈다() {
        newStudent(token + "가", 3, 5, 31);
        newStudent(token + "나", 3, 5, 31);
        newStudent(token + "다", 3, 5, 31);

        assertThat(queryUserSearchService.execute("3531", 0, 2).users()).hasSize(2);
        assertThat(queryUserSearchService.execute("3531", 1, 2).users()).hasSize(1);
    }

    @Test
    void 이름의_일부로_찾고_이름순으로_나온다() {
        User second = newUser(token + "나", Role.STUDENT);
        User first = newUser(token + "가", Role.STUDENT);
        newUser("other-" + UUID.randomUUID(), Role.STUDENT);

        UserSearchListResponse response = queryUserSearchService.execute(token, 0, 20);

        assertThat(response.users()).extracting(UserSearchResponse::userId)
            .containsExactly(first.getUserId(), second.getUserId());
    }

    @Test
    void 이름은_대소문자를_구분하지_않는다() {
        User user = newUser("Kim-" + token, Role.STUDENT);

        assertThat(queryUserSearchService.execute("kIM-" + token.toUpperCase(), 0, 20).users())
            .extracting(UserSearchResponse::userId).containsExactly(user.getUserId());
    }

    @Test
    void 키워드_앞뒤_공백은_무시한다() {
        User user = newUser(token + "가", Role.STUDENT);

        assertThat(queryUserSearchService.execute("  " + token + " ", 0, 20).users())
            .extracting(UserSearchResponse::userId).containsExactly(user.getUserId());
    }

    @Test
    void 숫자_4자리는_학번이_같은_학생만_찾는다() {
        User target = newStudent(token + "가", 3, 4, 12);
        User sameClassOtherNumber = newStudent(token + "나", 3, 4, 13);
        User otherClassSameNumber = newStudent(token + "다", 3, 3, 12);

        List<Long> found = queryUserSearchService.execute("3412", 0, 100).users().stream()
            .map(UserSearchResponse::userId)
            .toList();

        assertThat(found).contains(target.getUserId())
            .doesNotContain(sameClassOtherNumber.getUserId(), otherClassSameNumber.getUserId());
    }

    @Test
    void 번호가_한_자리인_학번도_찾는다() {
        User target = newStudent(token + "가", 1, 2, 5);

        assertThat(queryUserSearchService.execute("1205", 0, 100).users())
            .extracting(UserSearchResponse::userId).contains(target.getUserId());
    }

    @Test
    void 숫자_4자리는_이름이_아니라_학번으로만_찾는다() {
        // 이름이 "9876"인 유저는 학번 9876인 학생이 없으니 "9876"으로 검색해도 나오지 않는다
        User namedAsNumber = newUser("9876", Role.STUDENT);

        assertThat(queryUserSearchService.execute("9876", 0, 100).users())
            .extracting(UserSearchResponse::userId).doesNotContain(namedAsNumber.getUserId());
    }

    @Test
    void 숫자가_섞인_키워드는_이름으로_찾는다() {
        User user = newUser(token + "12", Role.STUDENT);

        assertThat(queryUserSearchService.execute(token + "12", 0, 20).users())
            .extracting(UserSearchResponse::userId).containsExactly(user.getUserId());
    }

    @Test
    void 나_자신은_결과에서_빠진다() {
        assertThat(queryUserSearchService.execute("me-" + token, 0, 20).users()).isEmpty();
    }

    @Test
    void 학번으로_찾을_때도_나_자신은_빠진다() {
        studentOf(me, 3, 4, 77);

        assertThat(queryUserSearchService.execute("3477", 0, 100).users())
            .extracting(UserSearchResponse::userId).doesNotContain(me.getUserId());
    }

    @Test
    void 학생은_학년_반_번호가_나오고_선생님은_student가_null이다() {
        User student = newStudent(token + "학생", 2, 3, 9);
        User teacher = newUser(token + "선생", Role.TEACHER);

        List<UserSearchResponse> users = queryUserSearchService.execute(token, 0, 20).users();

        UserSearchResponse studentResponse = users.stream()
            .filter(user -> user.userId().equals(student.getUserId())).findFirst().orElseThrow();
        UserSearchResponse teacherResponse = users.stream()
            .filter(user -> user.userId().equals(teacher.getUserId())).findFirst().orElseThrow();
        assertThat(studentResponse.name()).isEqualTo(token + "학생");
        assertThat(studentResponse.rankName()).isEqualTo("죽순");
        assertThat(studentResponse.student().grade()).isEqualTo((short) 2);
        assertThat(studentResponse.student().classNumber()).isEqualTo((short) 3);
        assertThat(studentResponse.student().number()).isEqualTo((short) 9);
        assertThat(teacherResponse.student()).isNull();
    }

    @Test
    void 일치하는_유저가_없으면_빈_목록이다() {
        UserSearchListResponse response = queryUserSearchService.execute("none-" + UUID.randomUUID(), 0, 20);

        assertThat(response.users()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void 페이지를_나눈다() {
        newUser(token + "가", Role.STUDENT);
        newUser(token + "나", Role.STUDENT);
        newUser(token + "다", Role.STUDENT);

        UserSearchListResponse page0 = queryUserSearchService.execute(token, 0, 2);
        UserSearchListResponse page1 = queryUserSearchService.execute(token, 1, 2);

        assertThat(page0.users()).extracting(UserSearchResponse::name).containsExactly(token + "가", token + "나");
        assertThat(page1.users()).extracting(UserSearchResponse::name).containsExactly(token + "다");
        assertThat(page0.totalElements()).isEqualTo(3);
        assertThat(page0.totalPages()).isEqualTo(2);
    }

    private User newUser(String name, Role role) {
        User user = userRepository.save(User.builder()
            .email(UUID.randomUUID().toString().substring(0, 12) + "@dsm.hs.kr")
            .password("password")
            .name(name)
            .role(role)
            .rank(userRankRepository.findByName("죽순").orElseThrow())
            .build());
        created.add(user);
        return user;
    }

    private User newStudent(String name, int grade, int classNumber, int number) {
        return studentOf(newUser(name, Role.STUDENT), grade, classNumber, number);
    }

    private User studentOf(User user, int grade, int classNumber, int number) {
        studentRepository.save(Student.builder()
            .user(user)
            .grade((short) grade)
            .classNumber((short) classNumber)
            .studentNumber((short) number)
            .build());
        return user;
    }
}
