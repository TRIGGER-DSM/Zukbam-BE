package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.category.domain.repository.ProblemCategoryRepository;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryProblemListService {

    private final ProblemRepository problemRepository;
    private final ProblemCategoryRepository problemCategoryRepository;

    @Transactional(readOnly = true)
    public ProblemListResponse execute(int page, int size) {
        Page<Problem> problems = problemRepository.findAll(PageRequest.of(page, size, Sort.by("problemId").ascending()));
        List<Long> problemIds = problems.getContent().stream().map(Problem::getProblemId).toList();

        // 문제마다 분류를 조회하지 않도록 이 페이지 문제들의 분류를 한 번에 가져온다
        Map<Long, List<CategoryResponse>> categoriesByProblemId = problemCategoryRepository.findAllByProblemIds(problemIds).stream()
            .collect(Collectors.groupingBy(
                problemCategory -> problemCategory.getId().getProblemId(),
                Collectors.mapping(problemCategory -> CategoryResponse.from(problemCategory.getCategory()), Collectors.toList())
            ));

        return ProblemListResponse.of(problems, categoriesByProblemId);
    }
}
