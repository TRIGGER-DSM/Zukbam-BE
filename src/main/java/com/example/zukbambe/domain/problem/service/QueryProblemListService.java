package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryProblemListService {

    private final ProblemRepository problemRepository;

    @Transactional(readOnly = true)
    public ProblemListResponse execute(int page, int size) {
        return ProblemListResponse.from(
            problemRepository.findAll(PageRequest.of(page, size, Sort.by("problemId").ascending()))
        );
    }
}
