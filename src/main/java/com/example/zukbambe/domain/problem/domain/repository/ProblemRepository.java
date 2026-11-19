package com.example.zukbambe.domain.problem.domain.repository;

import com.example.zukbambe.domain.problem.domain.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    // 목록에서 랭크를 건건이 조회하지 않도록 함께 가져온다
    @Override
    @EntityGraph(attributePaths = "rank")
    Page<Problem> findAll(Pageable pageable);

    boolean existsByTitle(String title);

    boolean existsByTitleAndProblemIdNot(String title, Long problemId);
}
