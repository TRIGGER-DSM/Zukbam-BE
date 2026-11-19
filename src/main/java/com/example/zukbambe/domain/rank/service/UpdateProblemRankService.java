package com.example.zukbambe.domain.rank.service;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.exception.ProblemRankNameDuplicatedException;
import com.example.zukbambe.domain.rank.facade.ProblemRankFacade;
import com.example.zukbambe.domain.rank.presentation.dto.request.UpdateProblemRankRequest;
import com.example.zukbambe.domain.xp.facade.XpFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProblemRankService {

    private final ProblemRankFacade problemRankFacade;
    private final ProblemRankRepository problemRankRepository;
    private final XpFacade xpFacade;

    @Transactional
    public void execute(Long problemRankId, UpdateProblemRankRequest request) {
        ProblemRank rank = problemRankFacade.getProblemRankById(problemRankId);

        if (problemRankRepository.existsByNameAndProblemRankIdNot(request.name(), problemRankId)) {
            throw new ProblemRankNameDuplicatedException();
        }

        long diff = request.xp() - rank.getXp();
        rank.update(request.name(), request.xp());

        if (diff != 0) {
            xpFacade.compensateProblemRankSolvers(rank, diff);
        }
    }
}
