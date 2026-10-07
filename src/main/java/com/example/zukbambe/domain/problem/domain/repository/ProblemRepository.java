package com.example.zukbambe.domain.problem.domain.repository;

import com.example.zukbambe.domain.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndProblemIdNot(String title, Long problemId);
}
