package com.example.zukbambe.domain.solution.domain.repository;

import com.example.zukbambe.domain.solution.domain.Solution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    @Query("""
        select s
        from Solution s
        join fetch s.problem
        where s.solutionId = :solutionId
        """)
    Optional<Solution> findWithProblemBySolutionId(@Param("solutionId") Long solutionId);

    @Query("""
        select s
        from Solution s
        where (:problemId is null or s.problem.problemId = :problemId)
          and (:email is null or s.user.email = :email)
        """)
    Page<Solution> findAllByFilter(
        @Param("problemId") Long problemId,
        @Param("email") String email,
        Pageable pageable
    );
}
