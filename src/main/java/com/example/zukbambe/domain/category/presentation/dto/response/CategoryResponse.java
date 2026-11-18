package com.example.zukbambe.domain.category.presentation.dto.response;

import com.example.zukbambe.domain.category.domain.Category;

public record CategoryResponse(
    Long categoryId,
    String name
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getCategoryId(), category.getName());
    }
}
