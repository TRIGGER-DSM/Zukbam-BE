package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.event.SolutionSubmittedEvent;
import com.example.zukbambe.domain.solution.presentation.dto.request.SubmitSolutionRequest;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitSolutionService {

    private final SolutionRepository solutionRepository;
    private final UserFacade userFacade;
    private final ProblemFacade problemFacade;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long execute(Long problemId, SubmitSolutionRequest request) {
        User user = userFacade.getCurrentUser();
        Problem problem = problemFacade.getProblemById(problemId);

        Solution solution = solutionRepository.save(Solution.of(user, problem, request.language(), request.code()));
        eventPublisher.publishEvent(new SolutionSubmittedEvent(solution.getSolutionId()));

        return solution.getSolutionId();
    }
}
