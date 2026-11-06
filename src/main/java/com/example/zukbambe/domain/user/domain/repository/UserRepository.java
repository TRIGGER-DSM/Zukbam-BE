package com.example.zukbambe.domain.user.domain.repository;

import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 읽고 더해서 저장하면 동시 지급 때 한쪽이 사라진다. 한 문장으로 더하고 랭크도 같은 값으로 다시 고른다
    @Modifying
    @Query(
        value = """
            update tbl_users
            set xp = xp + :amount,
                user_rank_id = (
                    select r.user_rank_id from tbl_user_ranks r
                    where r.score <= xp + :amount
                    order by r.score desc limit 1
                )
            where user_id = :userId
            """,
        nativeQuery = true
    )
    int addXp(@Param("userId") Long userId, @Param("amount") long amount);

    @Modifying
    @Query(
        value = """
            update tbl_users
            set xp = xp + :amount,
                user_rank_id = (
                    select r.user_rank_id from tbl_user_ranks r
                    where r.score <= xp + :amount
                    order by r.score desc limit 1
                )
            where user_id in (select sh.user_id from tbl_solved_histories sh where sh.problem_id = :problemId)
            """,
        nativeQuery = true
    )
    int addXpToProblemSolvers(@Param("problemId") Long problemId, @Param("amount") long amount);

    // 그 랭크 문제를 푼 개수만큼 차액을 곱해서 더한다
    @Modifying
    @Query(
        value = """
            update tbl_users u
            set xp = u.xp + :amount * c.solved_count,
                user_rank_id = (
                    select r.user_rank_id from tbl_user_ranks r
                    where r.score <= u.xp + :amount * c.solved_count
                    order by r.score desc limit 1
                )
            from (
                select sh.user_id, count(*) as solved_count
                from tbl_solved_histories sh
                join tbl_problems p on p.problem_id = sh.problem_id
                where p.problem_rank_id = :problemRankId
                group by sh.user_id
            ) c
            where c.user_id = u.user_id
            """,
        nativeQuery = true
    )
    int addXpToProblemRankSolvers(@Param("problemRankId") Long problemRankId, @Param("amount") long amount);
}
