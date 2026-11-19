package com.example.zukbambe.domain.solvedhistory.domain.repository;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 친구가 푼 문제 중 내가 아직 안 푼 문제의 ID를, 친구가 가장 최근에 푼 순서로 가져온다
    @Query(
        value = """
            select s.id.problemId
            from SolvedHistory s
            where s.id.userId in :friendIds
              and s.id.problemId not in (select m.id.problemId from SolvedHistory m where m.id.userId = :userId)
            group by s.id.problemId
            order by max(s.solvedDate) desc, s.id.problemId desc
            """,
        countQuery = """
            select count(distinct s.id.problemId)
            from SolvedHistory s
            where s.id.userId in :friendIds
              and s.id.problemId not in (select m.id.problemId from SolvedHistory m where m.id.userId = :userId)
            """
    )
    Page<Long> findFriendSolvedProblemIds(
        @Param("userId") Long userId,
        @Param("friendIds") Collection<Long> friendIds,
        Pageable pageable
    );

    // 주어진 문제들을 푼 친구 기록을 최근에 푼 순서로 가져온다. 응답에 쓸 문제, 난이도, 친구, 친구 랭크를 한 번에 불러온다
    @Query("""
        select s
        from SolvedHistory s
        join fetch s.problem p
        join fetch p.rank
        join fetch s.user u
        join fetch u.rank
        where s.id.problemId in :problemIds
          and s.id.userId in :friendIds
        order by s.solvedDate desc, s.id.userId
        """)
    List<SolvedHistory> findFriendSolvedHistories(
        @Param("friendIds") Collection<Long> friendIds,
        @Param("problemIds") Collection<Long> problemIds
    );

    // 푼 사람 한 명당 문제 ID 한 줄씩 가져온다. 문제별로 세면 푼 사람 수가 된다
    @Query("""
        select s.id.problemId
        from SolvedHistory s
        where s.id.problemId in :problemIds
        """)
    List<Long> findSolverProblemIds(@Param("problemIds") Collection<Long> problemIds);
}
