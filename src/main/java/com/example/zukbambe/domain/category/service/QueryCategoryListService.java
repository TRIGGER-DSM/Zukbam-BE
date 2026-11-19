package com.example.zukbambe.domain.category.service;

import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryCategoryListService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryListResponse execute() {
        return new CategoryListResponse(categoryRepository.findAllWithProblemCount());
    }
}
