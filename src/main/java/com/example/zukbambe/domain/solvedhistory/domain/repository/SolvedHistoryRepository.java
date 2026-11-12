package com.example.zukbambe.domain.solvedhistory.domain.repository;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SolvedHistoryRepository extends JpaRepository<SolvedHistory, SolvedHistoryId> {

    long countByIdUserId(Long userId);

    // 확인 후 저장하면 같은 문제를 동시에 맞혔을 때 늦은 쪽이 PK 충돌로 롤백된다. 넣은 개수(1/0)로 처음 푼 문제인지 판단한다
    @Modifying
    @Query(
        value = """
            insert into tbl_solved_histories (user_id, problem_id, solved_time)
            values (:userId, :problemId, :solvedTime)
            on conflict (user_id, problem_id) do nothing
            """,
        nativeQuery = true
    )
    int insertIfAbsent(
        @Param("userId") Long userId,
        @Param("problemId") Long problemId,
        @Param("solvedTime") LocalDateTime solvedTime
    );

    @Query("""
        select s.solvedDate
        from SolvedHistory s
        where s.id.userId = :userId
          and s.solvedDate >= :start
          and s.solvedDate < :end
        """)
    List<LocalDateTime> findSolvedDatesByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("""
        select p.rank
        from SolvedHistory s
        join s.problem p
        where s.id.userId = :userId
          and s.solvedDate >= :start
          and s.solvedDate < :end
        """)
    List<ProblemRank> findSolvedProblemRanksByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("""
        select s.id.problemId
        from SolvedHistory s
        where s.id.userId = :userId
          and s.id.problemId in :problemIds
        """)
    Set<Long> findSolvedProblemIds(
        @Param("userId") Long userId,
        @Param("problemIds") Collection<Long> problemIds
    );
}
