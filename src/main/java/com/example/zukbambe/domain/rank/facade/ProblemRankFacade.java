package com.example.zukbambe.domain.rank.facade;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.exception.ProblemRankNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemRankFacade {

    private final ProblemRankRepository problemRankRepository;

    public ProblemRank getProblemRankById(Long problemRankId) {
        return problemRankRepository.findById(problemRankId)
            .orElseThrow(ProblemRankNotFoundException::new);
    }
}
