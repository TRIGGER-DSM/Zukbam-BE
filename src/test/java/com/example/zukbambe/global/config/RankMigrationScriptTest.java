package com.example.zukbambe.global.config;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankMigrationScriptTest {

    @Autowired
    private UserRankRepository userRankRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRankRepository problemRankRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    void 유저_랭크_7개가_필요_XP와_함께_들어간다() {
        assertThat(userRankRepository.findByName("죽순")).get().extracting(UserRank::getScore).isEqualTo(0);
        assertThat(userRankRepository.findByName("대나무 1m")).get().extracting(UserRank::getScore).isEqualTo(100);
        assertThat(userRankRepository.findByName("대나무 3m")).get().extracting(UserRank::getScore).isEqualTo(300);
        assertThat(userRankRepository.findByName("대나무 5m")).get().extracting(UserRank::getScore).isEqualTo(600);
        assertThat(userRankRepository.findByName("대나무 10m")).get().extracting(UserRank::getScore).isEqualTo(1600);
        assertThat(userRankRepository.findByName("금죽")).get().extracting(UserRank::getScore).isEqualTo(3000);
        assertThat(userRankRepository.findByName("오죽")).get().extracting(UserRank::getScore).isEqualTo(5000);
    }

    @Test
    void 스크립트를_다시_실행해도_중복되거나_DB에서_바꾼_값이_덮어써지지_않는다() {
        try {
            jdbcTemplate.update("update tbl_user_ranks set score = 700 where name = '대나무 5m'");
            runRankMigrationScript();
            assertThat(jdbcTemplate.queryForObject("select count(*) from tbl_user_ranks where name = '대나무 5m'", Long.class)).isEqualTo(1);
            assertThat(userRankRepository.findByName("대나무 5m")).get().extracting(UserRank::getScore).isEqualTo(700);
        } finally {
            // 다른 테스트가 같은 DB를 쓰므로 원래 값으로 되돌린다
            jdbcTemplate.update("update tbl_user_ranks set score = 600 where name = '대나무 5m'");
        }
    }

    @Test
    void 예전_score_컬럼이_남아_있으면_xp로_옮긴다() {
        UserRank rank = userRankRepository.findByName("죽순").orElseThrow();
        User user = userRepository.save(User.builder()
            .email(UUID.randomUUID() + "@dsm.hs.kr")
            .password("password")
            .name("migrator")
            .role(Role.STUDENT)
            .rank(rank)
            .build());
        try {
            jdbcTemplate.execute("alter table tbl_users rename column xp to score");
            jdbcTemplate.update("update tbl_users set score = 42 where user_id = ?", user.getUserId());

            runRankMigrationScript();

            assertThat(jdbcTemplate.queryForObject("select xp from tbl_users where user_id = ?", Long.class, user.getUserId())).isEqualTo(42L);
            assertThat(columnExists("tbl_users", "score")).isFalse();
        } finally {
            if (!columnExists("tbl_users", "xp")) {
                jdbcTemplate.execute("alter table tbl_users rename column score to xp");
            }
            userRepository.deleteById(user.getUserId());
        }
    }

    @Test
    void 스크립트를_다시_실행해도_문제_랭크가_중복되지_않는다() {
        runRankMigrationScript();
        assertThat(jdbcTemplate.queryForObject("select count(*) from tbl_problem_ranks", Long.class)).isEqualTo(5);
        assertThat(problemRankRepository.findByName("비")).get().extracting(r -> r.getXp()).isEqualTo(80L);
    }

    @Test
    void 예전_LEVEL_문자열을_같은_순서의_문제_랭크로_옮긴다() {
        Problem problem = problemRepository.save(Problem.builder()
            .title("migration-" + UUID.randomUUID())
            .content("content")
            .timeLimit(1.5)
            .memoryLimit(131072L)
            .rank(problemRankRepository.findByName("이슬").orElseThrow())
            .build());
        try {
            // 예전 스키마 재현. 다른 테스트의 문제도 not null을 만족하도록 LEVEL_1로 채운다
            jdbcTemplate.execute("alter table tbl_problems drop column problem_rank_id");
            jdbcTemplate.execute("alter table tbl_problems add column problem_rank varchar(255)");
            jdbcTemplate.update("update tbl_problems set problem_rank = 'LEVEL_1'");
            jdbcTemplate.update("update tbl_problems set problem_rank = 'LEVEL_3' where problem_id = ?", problem.getProblemId());

            runRankMigrationScript();

            assertThat(jdbcTemplate.queryForObject(
                "select r.name from tbl_problems p join tbl_problem_ranks r on r.problem_rank_id = p.problem_rank_id where p.problem_id = ?",
                String.class, problem.getProblemId())).isEqualTo("물 한 병");
            assertThat(columnExists("tbl_problems", "problem_rank")).isFalse();
            assertThat(jdbcTemplate.queryForObject(
                "select is_nullable from information_schema.columns where table_schema = current_schema() and table_name = 'tbl_problems' and column_name = 'problem_rank_id'",
                String.class)).isEqualTo("NO");
            assertThat(jdbcTemplate.queryForObject(
                "select count(*) from pg_constraint where conname = 'fk_problems_problem_rank'", Long.class)).isEqualTo(1);
        } finally {
            problemRepository.deleteById(problem.getProblemId());
        }
    }

    private void runRankMigrationScript() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("db/rank-migration.sql"));
        populator.setSeparator("@@");
        populator.execute(dataSource);
    }

    private boolean columnExists(String table, String column) {
        Long count = jdbcTemplate.queryForObject(
            "select count(*) from information_schema.columns where table_schema = current_schema() and table_name = ? and column_name = ?",
            Long.class, table, column);
        return count != null && count > 0;
    }
}
