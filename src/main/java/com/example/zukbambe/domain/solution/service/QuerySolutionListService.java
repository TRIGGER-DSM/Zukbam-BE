package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.facade.ProblemFacade;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.presentation.dto.response.SolutionListResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuerySolutionListService {

    private final SolutionRepository solutionRepository;
    private final ProblemFacade problemFacade;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public SolutionListResponse execute(int page, int size, Long problemId, String email) {
        if (problemId != null) {
            problemFacade.getProblemById(problemId);
        }

        return SolutionListResponse.of(
            solutionRepository.findAllByFilter(
                problemId,
                email,
                PageRequest.of(page - 1, size, Sort.by("submittedAt").descending())
            ),
            userFacade.getCurrentUserId()
        );
    }
}
