package com.example.zukbambe.domain.category.presentation;

import com.example.zukbambe.domain.category.presentation.dto.request.CategoryRequest;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryListResponse;
import com.example.zukbambe.domain.category.service.CreateCategoryService;
import com.example.zukbambe.domain.category.service.DeleteCategoryService;
import com.example.zukbambe.domain.category.service.QueryCategoryListService;
import com.example.zukbambe.domain.category.service.UpdateCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryService createCategoryService;
    private final QueryCategoryListService queryCategoryListService;
    private final UpdateCategoryService updateCategoryService;
    private final DeleteCategoryService deleteCategoryService;

    /**
     * 분류 생성<p>
     * TEACHER 권한 필요. 이미 있는 이름은 쓸 수 없다.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(@RequestBody @Valid CategoryRequest request) {
        createCategoryService.execute(request);
    }

    /**
     * 분류 목록 조회<p>
     * 로그인하지 않아도 볼 수 있다. 이름순으로 나오고, 분류마다 그 분류가 붙은 문제 수가 함께 나온다.
     */
    @GetMapping
    public CategoryListResponse getCategories() {
        return queryCategoryListService.execute();
    }

    /**
     * 분류 수정<p>
     * TEACHER 권한 필요. 이미 있는 이름은 쓸 수 없다.
     */
    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(
        @PathVariable Long categoryId,
        @RequestBody @Valid CategoryRequest request
    ) {
        updateCategoryService.execute(categoryId, request);
    }

    /**
     * 분류 삭제<p>
     * TEACHER 권한 필요. 문제에 붙어 있던 분류도 같이 떨어진다.
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        deleteCategoryService.execute(categoryId);
    }
}
