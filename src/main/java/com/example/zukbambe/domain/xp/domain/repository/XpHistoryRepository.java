package com.example.zukbambe.domain.xp.domain.repository;

import com.example.zukbambe.domain.xp.domain.XpHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface XpHistoryRepository extends JpaRepository<XpHistory, Long> {

    Page<XpHistory> findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(Long userId, Pageable pageable);

    // 푼 사람이 많아도 한 번에 넣도록 insert-select로 처리한다
    @Modifying
    @Query(
        value = """
            insert into tbl_xp_histories (user_id, amount, reason, problem_id, problem_title, created_at)
            select sh.user_id, :amount, :reason, p.problem_id, p.title, :createdAt
            from tbl_solved_histories sh
            join tbl_problems p on p.problem_id = sh.problem_id
            where sh.problem_id = :problemId
            """,
        nativeQuery = true
    )
    int insertForProblemSolvers(
        @Param("problemId") Long problemId,
        @Param("amount") long amount,
        @Param("reason") String reason,
        @Param("createdAt") LocalDateTime createdAt
    );

    // 같은 랭크 문제를 여러 개 풀었으면 푼 문제마다 한 줄씩 남긴다
    @Modifying
    @Query(
        value = """
            insert into tbl_xp_histories (user_id, amount, reason, problem_id, problem_title, created_at)
            select sh.user_id, :amount, :reason, p.problem_id, p.title, :createdAt
            from tbl_solved_histories sh
            join tbl_problems p on p.problem_id = sh.problem_id
            where p.problem_rank_id = :problemRankId
            """,
        nativeQuery = true
    )
    int insertForProblemRankSolvers(
        @Param("problemRankId") Long problemRankId,
        @Param("amount") long amount,
        @Param("reason") String reason,
        @Param("createdAt") LocalDateTime createdAt
    );
}
