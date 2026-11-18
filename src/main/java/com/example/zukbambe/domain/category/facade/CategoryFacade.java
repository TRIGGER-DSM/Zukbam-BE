package com.example.zukbambe.domain.category.facade;

import com.example.zukbambe.domain.category.domain.Category;
import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryRepository categoryRepository;

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);
    }

    // 같은 ID가 여러 번 와도 한 번만 조회하고, 하나라도 없으면 실패한다
    public List<Category> getCategoriesByIds(Collection<Long> categoryIds) {
        List<Long> distinctIds = categoryIds.stream().distinct().toList();
        List<Category> categories = categoryRepository.findAllById(distinctIds);
        if (categories.size() != distinctIds.size()) {
            throw new CategoryNotFoundException();
        }
        return categories;
    }
}
