package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.exception.SolutionNotFoundException;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionResponse;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuerySolutionDetailService {

    private final SolutionRepository solutionRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public SolutionResponse execute(Long solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
            .orElseThrow(SolutionNotFoundException::new);
        User currentUser = userFacade.getCurrentUser();
        return SolutionResponse.of(solution, canViewCode(solution, currentUser));
    }

    // 선생님이거나, 내 풀이이거나, 그 풀이의 문제를 내가 푼 적이 있으면 코드를 볼 수 있다
    private boolean canViewCode(Solution solution, User currentUser) {
        Long currentUserId = currentUser.getUserId();
        return currentUser.getRole() == Role.TEACHER
            || solution.getUser().getUserId().equals(currentUserId)
            || solvedHistoryRepository.existsById(new SolvedHistoryId(currentUserId, solution.getProblem().getProblemId()));
    }
}
