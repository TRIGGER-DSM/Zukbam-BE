package com.example.zukbambe.domain.category.service;

import com.example.zukbambe.domain.category.domain.Category;
import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.domain.repository.ProblemCategoryRepository;
import com.example.zukbambe.domain.category.facade.CategoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCategoryService {

    private final CategoryFacade categoryFacade;
    private final CategoryRepository categoryRepository;
    private final ProblemCategoryRepository problemCategoryRepository;

    @Transactional
    public void execute(Long categoryId) {
        Category category = categoryFacade.getCategoryById(categoryId);

        problemCategoryRepository.deleteAllByCategoryId(categoryId);
        categoryRepository.delete(category);
    }
}
