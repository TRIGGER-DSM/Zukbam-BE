package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.category.domain.repository.CategoryRepository;
import com.example.zukbambe.domain.category.exception.CategoryNotFoundException;
import com.example.zukbambe.domain.category.presentation.dto.request.CategoryRequest;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryResponse;
import com.example.zukbambe.domain.category.service.CreateCategoryService;
import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.request.TestCaseRequest;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemSummaryResponse;
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
class ProblemCategoryTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private CreateProblemService createProblemService;
    @Autowired
    private UpdateProblemService updateProblemService;
    @Autowired
    private DeleteProblemService deleteProblemService;
    @Autowired
    private QueryProblemDetailService queryProblemDetailService;
    @Autowired
    private QueryProblemListService queryProblemListService;
    @Autowired
    private CreateCategoryService createCategoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemRankRepository problemRankRepository;

    @Test
    void 분류를_붙여_만들면_상세에_이름순으로_나온다() {
        String prefix = UUID.randomUUID().toString().substring(0, 8);
        Long second = newCategory(prefix + "나");
        Long first = newCategory(prefix + "가");

        Long problemId = createProblemService.execute(request("title-" + UUID.randomUUID(), List.of(second, first)));

        assertThat(categoryIdsOf(problemId)).containsExactly(first, second);
    }

    @Test
    void 분류를_안_보내면_분류_없이_만들어진다() {
        Long problemId = createProblemService.execute(request("title-" + UUID.randomUUID(), null));

        assertThat(categoryIdsOf(problemId)).isEmpty();
    }

    @Test
    void 같은_분류를_여러_번_보내도_한_번만_붙는다() {
        Long categoryId = newCategory("수학-" + UUID.randomUUID());

        Long problemId = createProblemService.execute(
            request("title-" + UUID.randomUUID(), List.of(categoryId, categoryId)));

        assertThat(categoryIdsOf(problemId)).containsExactly(categoryId);
    }

    @Test
    void 없는_분류로_만들면_실패하고_문제도_만들어지지_않는다() {
        String title = "title-" + UUID.randomUUID();

        assertThatThrownBy(() -> createProblemService.execute(request(title, List.of(-1L))))
            .isInstanceOf(CategoryNotFoundException.class);
        assertThat(problemRepository.existsByTitle(title)).isFalse();
    }

    @Test
    void 수정하면_분류가_요청한_것으로_바뀐다() {
        Long kept = newCategory("유지-" + UUID.randomUUID());
        Long removed = newCategory("제거-" + UUID.randomUUID());
        Long added = newCategory("추가-" + UUID.randomUUID());
        String title = "title-" + UUID.randomUUID();
        Long problemId = createProblemService.execute(request(title, List.of(kept, removed)));

        updateProblemService.execute(problemId, request(title, List.of(kept, added)));

        assertThat(categoryIdsOf(problemId)).containsExactlyInAnyOrder(kept, added);
    }

    @Test
    void 수정할_때_분류를_안_보내면_분류가_모두_떨어진다() {
        Long categoryId = newCategory("수학-" + UUID.randomUUID());
        String title = "title-" + UUID.randomUUID();
        Long problemId = createProblemService.execute(request(title, List.of(categoryId)));

        updateProblemService.execute(problemId, request(title, null));

        assertThat(categoryIdsOf(problemId)).isEmpty();
    }

    @Test
    void 없는_분류로_수정하면_실패하고_기존_분류는_그대로다() {
        Long categoryId = newCategory("수학-" + UUID.randomUUID());
        String title = "title-" + UUID.randomUUID();
        Long problemId = createProblemService.execute(request(title, List.of(categoryId)));

        assertThatThrownBy(() -> updateProblemService.execute(problemId, request(title, List.of(-1L))))
            .isInstanceOf(CategoryNotFoundException.class);
        assertThat(categoryIdsOf(problemId)).containsExactly(categoryId);
    }

    @Test
    void 목록에도_문제마다_분류가_나온다() {
        Long categoryId = newCategory("수학-" + UUID.randomUUID());
        Long withCategory = createProblemService.execute(request("title-" + UUID.randomUUID(), List.of(categoryId)));
        Long withoutCategory = createProblemService.execute(request("title-" + UUID.randomUUID(), List.of()));
        int total = (int) queryProblemListService.execute(0, 1).totalElements();

        // 문제 번호순이라 방금 만든 두 문제가 마지막 두 개다
        ProblemSummaryResponse second = queryProblemListService.execute(total - 2, 1).problems().getFirst();
        ProblemSummaryResponse last = queryProblemListService.execute(total - 1, 1).problems().getFirst();

        assertThat(second.problemId()).isEqualTo(withCategory);
        assertThat(second.categories()).extracting(CategoryResponse::categoryId).containsExactly(categoryId);
        assertThat(last.problemId()).isEqualTo(withoutCategory);
        assertThat(last.categories()).isEmpty();
    }

    @Test
    void 분류가_붙은_문제를_지워도_분류는_남는다() {
        Long categoryId = newCategory("수학-" + UUID.randomUUID());
        Long problemId = createProblemService.execute(request("title-" + UUID.randomUUID(), List.of(categoryId)));

        deleteProblemService.execute(problemId);

        assertThat(problemRepository.existsById(problemId)).isFalse();
        assertThat(categoryRepository.existsById(categoryId)).isTrue();
    }

    private List<Long> categoryIdsOf(Long problemId) {
        return queryProblemDetailService.execute(problemId).categories().stream()
            .map(CategoryResponse::categoryId)
            .toList();
    }

    private Long newCategory(String name) {
        return createCategoryService.execute(new CategoryRequest(name));
    }

    private ProblemRequest request(String title, List<Long> categoryIds) {
        return new ProblemRequest(title, "content", 131072L, 1.5,
            problemRankRepository.findByName("이슬").orElseThrow().getProblemRankId(),
            List.of(new TestCaseRequest("1 2", "3", true)), categoryIds);
    }
}
