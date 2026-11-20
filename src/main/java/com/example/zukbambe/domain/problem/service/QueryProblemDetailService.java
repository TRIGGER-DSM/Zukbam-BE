package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.category.domain.repository.ProblemCategoryRepository;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.testcase.domain.TestCase;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryProblemDetailService {

    private final ProblemFacade problemFacade;
    private final UserFacade userFacade;
    private final TestCaseRepository testCaseRepository;
    private final ProblemCategoryRepository problemCategoryRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final SolutionRepository solutionRepository;

    @Transactional(readOnly = true)
    public ProblemDetailResponse execute(Long problemId) {
        Problem problem = problemFacade.getProblemById(problemId);
        boolean isTeacher = userFacade.findCurrentUser()
            .map(user -> user.getRole() == Role.TEACHER)
            .orElse(false);

        // 채점용(숨김) 테스트케이스는 선생님에게만 노출하고, 비로그인 사용자는 학생과 같게 본다
        List<TestCase> testCases = isTeacher
            ? testCaseRepository.findAllByProblemOrderByTestcaseIdAsc(problem)
            : testCaseRepository.findAllByProblemAndIsSampleTrueOrderByTestcaseIdAsc(problem);

        List<CategoryResponse> categories = problemCategoryRepository.findAllByProblemIds(List.of(problemId)).stream()
            .map(problemCategory -> CategoryResponse.from(problemCategory.getCategory()))
            .toList();

        // 목록과 같은 쿼리로 센다. 푼 사람 한 명, 제출 한 번이 한 줄이다
        long solverCount = solvedHistoryRepository.findSolverProblemIds(List.of(problemId)).size();
        long attemptCount = solutionRepository.findAttemptProblemIds(List.of(problemId)).size();
        Double averageAttempts = solverCount == 0 ? null : (double) attemptCount / solverCount;

        return ProblemDetailResponse.of(problem, categories, solverCount, averageAttempts, testCases);
    }
}
