package com.example.zukbambe.domain.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ProblemCategoryId implements Serializable {

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "category_id")
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProblemCategoryId that)) return false;
        return Objects.equals(problemId, that.problemId)
                && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, categoryId);
    }
}
