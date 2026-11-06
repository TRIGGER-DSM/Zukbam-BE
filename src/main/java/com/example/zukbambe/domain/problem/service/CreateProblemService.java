package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.exception.ProblemTitleDuplicatedException;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.facade.ProblemRankFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProblemService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemRankFacade problemRankFacade;

    @Transactional
    public Long execute(ProblemRequest request) {
        if (problemRepository.existsByTitle(request.title())) {
            throw new ProblemTitleDuplicatedException();
        }

        ProblemRank rank = problemRankFacade.getProblemRankById(request.problemRankId());

        Problem problem = problemRepository.save(
            Problem.builder()
                .title(request.title())
                .content(request.content())
                .memoryLimit(request.memoryLimit())
                .timeLimit(request.timeLimit())
                .rank(rank)
                .build()
        );

        testCaseRepository.saveAll(
            request.testCases().stream()
                .map(testCase -> TestCase.of(problem, testCase.input(), testCase.output(), testCase.isSample()))
                .toList()
        );

        return problem.getProblemId();
    }
}
