package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.category.domain.repository.ProblemCategoryRepository;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.enums.ProblemSolveStatus;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryProblemListService {

    private final ProblemRepository problemRepository;
    private final ProblemCategoryRepository problemCategoryRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final SolutionRepository solutionRepository;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public ProblemListResponse execute(int page, int size) {
        Page<Problem> problems = problemRepository.findAll(PageRequest.of(page, size, Sort.by("problemId").ascending()));
        List<Long> problemIds = problems.getContent().stream().map(Problem::getProblemId).toList();

        // 문제마다 분류를 조회하지 않도록 이 페이지 문제들의 분류를 한 번에 가져온다
        Map<Long, List<CategoryResponse>> categoriesByProblemId = problemCategoryRepository.findAllByProblemIds(problemIds).stream()
            .collect(Collectors.groupingBy(
                problemCategory -> problemCategory.getId().getProblemId(),
                Collectors.mapping(problemCategory -> CategoryResponse.from(problemCategory.getCategory()), Collectors.toList())
            ));

        // 푼 사람 수와 평균 시도도 이 페이지 문제들을 한 번에 센다. 가져온 한 줄이 푼 사람 한 명, 제출 한 번이다
        Map<Long, Long> solverCountByProblemId = countByProblemId(solvedHistoryRepository.findSolverProblemIds(problemIds));
        Map<Long, Long> attemptCountByProblemId = countByProblemId(solutionRepository.findAttemptProblemIds(problemIds));
        Map<Long, Double> averageAttemptsByProblemId = solverCountByProblemId.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> (double) attemptCountByProblemId.getOrDefault(entry.getKey(), 0L) / entry.getValue()
            ));

        // 풀이 상태는 로그인한 유저만 알 수 있다
        Map<Long, ProblemSolveStatus> statusByProblemId = userFacade.findCurrentUser()
            .map(user -> solveStatuses(user, problemIds))
            .orElse(Map.of());

        return ProblemListResponse.of(
            problems, categoriesByProblemId, solverCountByProblemId, averageAttemptsByProblemId, statusByProblemId
        );
    }

    private Map<Long, Long> countByProblemId(List<Long> problemIds) {
        return problemIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Map<Long, ProblemSolveStatus> solveStatuses(User user, List<Long> problemIds) {
        Set<Long> solved = solvedHistoryRepository.findSolvedProblemIds(user.getUserId(), problemIds);
        Set<Long> attempted = solutionRepository.findAttemptedProblemIds(user.getUserId(), problemIds);

        return problemIds.stream().collect(Collectors.toMap(
            problemId -> problemId,
            problemId -> solved.contains(problemId) ? ProblemSolveStatus.SOLVED
                : attempted.contains(problemId) ? ProblemSolveStatus.ATTEMPTED
                : ProblemSolveStatus.UNATTEMPTED
        ));
    }
}
