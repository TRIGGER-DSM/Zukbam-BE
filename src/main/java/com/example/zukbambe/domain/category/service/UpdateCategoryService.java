package com.example.zukbambe.domain.category.service;

import com.example.zukbambe.domain.category.domain.Category;
import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.exception.CategoryNameDuplicatedException;
import com.example.zukbambe.domain.category.facade.CategoryFacade;
import com.example.zukbambe.domain.category.presentation.dto.request.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCategoryService {

    private final CategoryFacade categoryFacade;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void execute(Long categoryId, CategoryRequest request) {
        Category category = categoryFacade.getCategoryById(categoryId);

        if (categoryRepository.existsByNameAndCategoryIdNot(request.name(), categoryId)) {
            throw new CategoryNameDuplicatedException();
        }

        category.update(request.name());
    }
}
