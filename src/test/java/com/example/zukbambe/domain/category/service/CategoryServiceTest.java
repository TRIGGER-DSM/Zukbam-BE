package com.example.zukbambe.domain.category.service;

import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.domain.repository.ProblemCategoryRepository;
import com.example.zukbambe.domain.category.exception.CategoryNameDuplicatedException;
import com.example.zukbambe.domain.category.exception.CategoryNotFoundException;
import com.example.zukbambe.domain.category.presentation.dto.request.CategoryRequest;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryProblemCountResponse;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.request.TestCaseRequest;
import com.example.zukbambe.domain.problem.service.CreateProblemService;
import com.example.zukbambe.domain.problem.service.DeleteProblemService;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CategoryServiceTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private CreateCategoryService createCategoryService;
    @Autowired
    private QueryCategoryListService queryCategoryListService;
    @Autowired
    private UpdateCategoryService updateCategoryService;
    @Autowired
    private DeleteCategoryService deleteCategoryService;
    @Autowired
    private CreateProblemService createProblemService;
    @Autowired
    private DeleteProblemService deleteProblemService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProblemCategoryRepository problemCategoryRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;

    @Test
    void 분류를_만들면_문제_수_0으로_목록에_나온다() {
        Long categoryId = createCategoryService.execute(new CategoryRequest(uniqueName("수학")));

        assertThat(countOf(categoryId).problemCount()).isZero();
    }

    @Test
    void 이미_있는_이름으로_만들면_실패한다() {
        String name = uniqueName("수학");
        createCategoryService.execute(new CategoryRequest(name));

        assertThatThrownBy(() -> createCategoryService.execute(new CategoryRequest(name)))
            .isInstanceOf(CategoryNameDuplicatedException.class);
    }

    @Test
    void 분류_이름을_바꿀_수_있다() {
        Long categoryId = createCategoryService.execute(new CategoryRequest(uniqueName("수학")));
        String newName = uniqueName("구현");

        updateCategoryService.execute(categoryId, new CategoryRequest(newName));

        assertThat(countOf(categoryId).name()).isEqualTo(newName);
    }

    @Test
    void 다른_분류와_같은_이름으로는_못_바꾸고_자기_이름으로는_바꿀_수_있다() {
        String name = uniqueName("수학");
        Long categoryId = createCategoryService.execute(new CategoryRequest(name));
        Long otherId = createCategoryService.execute(new CategoryRequest(uniqueName("구현")));

        assertThatThrownBy(() -> updateCategoryService.execute(otherId, new CategoryRequest(name)))
            .isInstanceOf(CategoryNameDuplicatedException.class);

        updateCategoryService.execute(categoryId, new CategoryRequest(name));
        assertThat(countOf(categoryId).name()).isEqualTo(name);
    }

    @Test
    void 없는_분류를_수정하거나_삭제하면_실패한다() {
        assertThatThrownBy(() -> updateCategoryService.execute(-1L, new CategoryRequest("이름")))
            .isInstanceOf(CategoryNotFoundException.class);
        assertThatThrownBy(() -> deleteCategoryService.execute(-1L))
            .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void 목록은_분류별_문제_수를_이름순으로_보여준다() {
        String prefix = UUID.randomUUID().toString().substring(0, 8);
        Long second = createCategoryService.execute(new CategoryRequest(prefix + "나"));
        Long first = createCategoryService.execute(new CategoryRequest(prefix + "가"));
        createProblem(List.of(first, second));
        createProblem(List.of(first));

        List<CategoryProblemCountResponse> mine = queryCategoryListService.execute().categories().stream()
            .filter(category -> category.name().startsWith(prefix))
            .toList();

        assertThat(mine).extracting(CategoryProblemCountResponse::categoryId).containsExactly(first, second);
        assertThat(mine).extracting(CategoryProblemCountResponse::problemCount).containsExactly(2L, 1L);
    }

    @Test
    void 문제를_지우면_분류의_문제_수가_줄어든다() {
        Long categoryId = createCategoryService.execute(new CategoryRequest(uniqueName("수학")));
        Long problemId = createProblem(List.of(categoryId));
        createProblem(List.of(categoryId));
        assertThat(countOf(categoryId).problemCount()).isEqualTo(2L);

        deleteProblemService.execute(problemId);

        assertThat(countOf(categoryId).problemCount()).isEqualTo(1L);
    }

    @Test
    void 분류를_지우면_문제에서도_떨어지고_문제는_남는다() {
        Long categoryId = createCategoryService.execute(new CategoryRequest(uniqueName("수학")));
        Long keptId = createCategoryService.execute(new CategoryRequest(uniqueName("구현")));
        Long problemId = createProblem(List.of(categoryId, keptId));

        deleteCategoryService.execute(categoryId);

        assertThat(categoryRepository.existsById(categoryId)).isFalse();
        assertThat(problemCategoryRepository.findAllByProblemIds(List.of(problemId)))
            .extracting(problemCategory -> problemCategory.getCategory().getCategoryId())
            .containsExactly(keptId);
    }

    private CategoryProblemCountResponse countOf(Long categoryId) {
        return queryCategoryListService.execute().categories().stream()
            .filter(category -> category.categoryId().equals(categoryId))
            .findFirst()
            .orElseThrow();
    }

    private Long createProblem(List<Long> categoryIds) {
        return createProblemService.execute(new ProblemRequest(
            "title-" + UUID.randomUUID(), "content", 131072L, 1.5,
            problemRankRepository.findByName("이슬").orElseThrow().getProblemRankId(),
            List.of(new TestCaseRequest("1 2", "3", true)),
            categoryIds
        ));
    }

    private String uniqueName(String name) {
        return name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
