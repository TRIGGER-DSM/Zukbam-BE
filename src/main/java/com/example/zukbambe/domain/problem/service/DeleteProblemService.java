package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProblemService {

    private final ProblemFacade problemFacade;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    @Transactional
    public void execute(Long problemId) {
        Problem problem = problemFacade.getProblemById(problemId);

        testCaseRepository.deleteAllByProblem(problem);
        problemRepository.delete(problem);
    }
}
