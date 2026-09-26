package com.example.zukbambe.domain.solvedhistory.domain;

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
public class SolvedHistoryId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "problem_id")
    private Long problemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolvedHistoryId that)) return false;
        return Objects.equals(userId, that.userId)
                && Objects.equals(problemId, that.problemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, problemId);
    }
}
