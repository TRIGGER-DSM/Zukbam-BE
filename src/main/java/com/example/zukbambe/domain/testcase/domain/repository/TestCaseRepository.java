package com.example.zukbambe.domain.testcase.domain.repository;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findAllByProblemOrderByTestcaseIdAsc(Problem problem);

    List<TestCase> findAllByProblemAndIsSampleTrueOrderByTestcaseIdAsc(Problem problem);

    void deleteAllByProblem(Problem problem);
}
