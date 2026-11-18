package com.example.zukbambe.domain.category.presentation.dto.response;

import java.util.List;

public record CategoryListResponse(
    List<CategoryProblemCountResponse> categories
) {
}
