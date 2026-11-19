package com.example.zukbambe.domain.rank.service;

import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryProblemRankListService {

    private final ProblemRankRepository problemRankRepository;

    @Transactional(readOnly = true)
    public ProblemRankListResponse execute() {
        return ProblemRankListResponse.from(problemRankRepository.findAllByOrderByXpAscProblemRankIdAsc());
    }
}
