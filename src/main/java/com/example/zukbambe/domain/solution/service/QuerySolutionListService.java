package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionListResponse;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuerySolutionListService {

    private final SolutionRepository solutionRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final ProblemFacade problemFacade;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public SolutionListResponse execute(int page, int size, Long problemId, String email) {
        if (problemId != null) {
            problemFacade.getProblemById(problemId);
        }

        Long currentUserId = userFacade.getCurrentUserId();
        Page<Solution> solutions = solutionRepository.findAllByFilter(
            problemId,
            email,
            PageRequest.of(page - 1, size, Sort.by("submittedAt").descending())
        );
        Set<Long> solvedProblemIds = findSolvedProblemIds(currentUserId, solutions);

        // 내 풀이이거나, 그 풀이의 문제를 내가 푼 적이 있으면 코드를 볼 수 있다
        return SolutionListResponse.of(
            solutions,
            solution -> solution.getUser().getUserId().equals(currentUserId)
                || solvedProblemIds.contains(solution.getProblem().getProblemId())
        );
    }

    private Set<Long> findSolvedProblemIds(Long userId, Page<Solution> solutions) {
        Set<Long> problemIds = solutions.getContent().stream()
            .map(solution -> solution.getProblem().getProblemId())
            .collect(Collectors.toSet());
        if (problemIds.isEmpty()) {
            return Set.of();
        }
        return solvedHistoryRepository.findSolvedProblemIds(userId, problemIds);
    }
}
