package com.example.zukbambe.domain.category.domain.repository;

import com.example.zukbambe.domain.category.domain.Category;
import com.example.zukbambe.domain.category.presentation.dto.response.CategoryProblemCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndCategoryIdNot(String name, Long categoryId);

    // 문제가 하나도 없는 분류도 0으로 나오도록 left join 한다
    @Query("""
        select new com.example.zukbambe.domain.category.presentation.dto.response.CategoryProblemCountResponse(
            c.categoryId, c.name, count(pc.id.problemId)
        )
        from Category c
        left join ProblemCategory pc on pc.category = c
        group by c.categoryId, c.name
        order by c.name
        """)
    List<CategoryProblemCountResponse> findAllWithProblemCount();
}
