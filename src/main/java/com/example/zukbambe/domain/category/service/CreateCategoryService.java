package com.example.zukbambe.domain.category.service;

import com.example.zukbambe.domain.category.domain.Category;
import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.exception.CategoryNameDuplicatedException;
import com.example.zukbambe.domain.category.presentation.dto.request.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long execute(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryNameDuplicatedException();
        }

        return categoryRepository.save(Category.builder().name(request.name()).build()).getCategoryId();
    }
}
