package com.example.zukbambe.global.config;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// 랭크 기본 데이터는 Flyway V1__init.sql이 넣는다
@SpringBootTest
class RankSeedMigrationTest {

    @Autowired
    private UserRankRepository userRankRepository;

    @Autowired
    private ProblemRankRepository problemRankRepository;

    @Test
    void 유저_랭크_7개가_필요_XP와_함께_들어간다() {
        assertThat(userRankRepository.findByName("죽순")).get().extracting(UserRank::getXp).isEqualTo(0);
        assertThat(userRankRepository.findByName("대나무 1m")).get().extracting(UserRank::getXp).isEqualTo(100);
        assertThat(userRankRepository.findByName("대나무 3m")).get().extracting(UserRank::getXp).isEqualTo(300);
        assertThat(userRankRepository.findByName("대나무 5m")).get().extracting(UserRank::getXp).isEqualTo(600);
        assertThat(userRankRepository.findByName("대나무 10m")).get().extracting(UserRank::getXp).isEqualTo(1600);
        assertThat(userRankRepository.findByName("금죽")).get().extracting(UserRank::getXp).isEqualTo(3000);
        assertThat(userRankRepository.findByName("오죽")).get().extracting(UserRank::getXp).isEqualTo(5000);
    }

    @Test
    void 문제_랭크_5개가_XP와_함께_들어간다() {
        assertThat(problemRankRepository.findByName("이슬")).get().extracting(ProblemRank::getXp).isEqualTo(10L);
        assertThat(problemRankRepository.findByName("물 한 컵")).get().extracting(ProblemRank::getXp).isEqualTo(20L);
        assertThat(problemRankRepository.findByName("물 한 병")).get().extracting(ProblemRank::getXp).isEqualTo(30L);
        assertThat(problemRankRepository.findByName("물 한 통")).get().extracting(ProblemRank::getXp).isEqualTo(50L);
        assertThat(problemRankRepository.findByName("비")).get().extracting(ProblemRank::getXp).isEqualTo(80L);
    }
}
