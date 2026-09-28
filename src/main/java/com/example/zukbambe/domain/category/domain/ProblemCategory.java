package com.example.zukbambe.domain.category.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_problem_categories")
public class ProblemCategory {

    @EmbeddedId
    private ProblemCategoryId id;

    @MapsId("problemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static ProblemCategory of(Problem problem, Category category) {
        return ProblemCategory.builder()
                .id(new ProblemCategoryId(problem.getProblemId(), category.getCategoryId()))
                .problem(problem)
                .category(category)
                .build();
    }
}
