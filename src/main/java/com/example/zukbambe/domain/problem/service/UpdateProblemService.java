package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.exception.ProblemTitleDuplicatedException;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProblemService {

    private final ProblemFacade problemFacade;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    @Transactional
    public void execute(Long problemId, ProblemRequest request) {
        Problem problem = problemFacade.getProblemById(problemId);

        if (problemRepository.existsByTitleAndProblemIdNot(request.title(), problemId)) {
            throw new ProblemTitleDuplicatedException();
        }

        problem.update(
            request.title(),
            request.content(),
            request.memoryLimit(),
            request.timeLimit(),
            request.rank()
        );

        testCaseRepository.deleteAllByProblem(problem);
        testCaseRepository.saveAll(
            request.testCases().stream()
                .map(testCase -> testCase.toEntity(problem))
                .toList()
        );
    }
}
