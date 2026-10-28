package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.exception.SolutionNotFoundException;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuerySolutionDetailService {

    private final SolutionRepository solutionRepository;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public SolutionResponse execute(Long solutionId) {
        return solutionRepository.findById(solutionId)
            .map(solution -> SolutionResponse.of(solution, userFacade.getCurrentUserId()))
            .orElseThrow(SolutionNotFoundException::new);
    }
}
