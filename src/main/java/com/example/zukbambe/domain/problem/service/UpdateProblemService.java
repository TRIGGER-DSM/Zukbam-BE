package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.exception.ProblemTitleDuplicatedException;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.facade.ProblemRankFacade;
import com.example.zukbambe.domain.xp.facade.XpFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProblemService {

    private final ProblemFacade problemFacade;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemRankFacade problemRankFacade;
    private final XpFacade xpFacade;

    @Transactional
    public void execute(Long problemId, ProblemRequest request) {
        Problem problem = problemFacade.getProblemById(problemId);

        if (problemRepository.existsByTitleAndProblemIdNot(request.title(), problemId)) {
            throw new ProblemTitleDuplicatedException();
        }

        long oldXp = problem.getRank().getXp();
        ProblemRank newRank = problemRankFacade.getProblemRankById(request.problemRankId());

        problem.update(
            request.title(),
            request.content(),
            request.memoryLimit(),
            request.timeLimit(),
            newRank
        );

        long diff = newRank.getXp() - oldXp;
        if (diff != 0) {
            xpFacade.compensateProblemSolvers(problem, diff);
        }

        testCaseRepository.deleteAllByProblem(problem);
        testCaseRepository.saveAll(
            request.testCases().stream()
                .map(testCase -> TestCase.of(problem, testCase.input(), testCase.output(), testCase.isSample()))
                .toList()
        );
    }
}
