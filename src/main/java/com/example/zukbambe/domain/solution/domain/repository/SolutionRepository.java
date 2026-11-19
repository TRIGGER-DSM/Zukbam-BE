package com.example.zukbambe.domain.solution.domain.repository;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    @Query("""
        select s
        from Solution s
        join fetch s.problem
        where s.solutionId = :solutionId
        """)
    Optional<Solution> findWithProblemBySolutionId(@Param("solutionId") Long solutionId);

    @Query(value = """
        select s
        from Solution s
        join fetch s.user u
        where (:problemId is null or s.problem.problemId = :problemId)
          and (:email is null or u.email = :email)
        """,
        countQuery = """
        select count(s)
        from Solution s
        join s.user u
        where (:problemId is null or s.problem.problemId = :problemId)
          and (:email is null or u.email = :email)
        """)
    Page<Solution> findAllByFilter(
        @Param("problemId") Long problemId,
        @Param("email") String email,
        Pageable pageable
    );

    @Query("select s.solutionId from Solution s where s.status = :status order by s.solutionId")
    List<Long> findIdsByStatus(@Param("status") Status status);

    @Query("""
        select s.language as language, s.status as status
        from Solution s
        where s.user.userId = :userId
          and s.submittedAt >= :start
          and s.submittedAt < :end
        """)
    List<SubmissionRow> findSubmissionsByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("""
        select distinct s.problem.problemId
        from Solution s
        where s.user.userId = :userId
          and s.problem.problemId in :problemIds
        """)
    Set<Long> findAttemptedProblemIds(
        @Param("userId") Long userId,
        @Param("problemIds") Collection<Long> problemIds
    );

    // 푼 사람이 처음 정답을 받기까지 낸 제출 한 번당 문제 ID 한 줄씩 가져온다. 순서는 시각 대신 solutionId로 비교한다
    @Query("""
        select s.problem.problemId
        from Solution s
        where s.problem.problemId in :problemIds
          and s.solutionId <= (
              select min(a.solutionId)
              from Solution a
              where a.problem = s.problem
                and a.user = s.user
                and a.status = com.example.zukbambe.domain.solution.domain.enums.Status.ACCEPTED
          )
        """)
    List<Long> findAttemptProblemIds(@Param("problemIds") Collection<Long> problemIds);
}
