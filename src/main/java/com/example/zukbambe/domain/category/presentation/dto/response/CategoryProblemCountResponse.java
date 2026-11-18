package com.example.zukbambe.domain.category.presentation.dto.response;

public record CategoryProblemCountResponse(
    Long categoryId,
    String name,
    long problemCount
) {
}
