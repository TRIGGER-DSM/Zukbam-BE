package com.example.zukbambe.domain.solution.domain.repository;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
}
