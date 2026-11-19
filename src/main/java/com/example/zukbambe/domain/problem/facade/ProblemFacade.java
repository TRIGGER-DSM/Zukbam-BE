package com.example.zukbambe.domain.problem.facade;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.exception.ProblemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemFacade {

    private final ProblemRepository problemRepository;

    public Problem getProblemById(Long problemId) {
        return problemRepository.findById(problemId)
            .orElseThrow(ProblemNotFoundException::new);
    }
}
