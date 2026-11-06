package com.example.zukbambe.domain.problem.domain;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    @Column(unique = true, nullable = false, name = "title")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false, name = "content")
    private String content;

    @Column(nullable = false, name = "memory_limit")
    private Long memoryLimit;

    @Column(nullable = false, name = "time_limit")
    private Double timeLimit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_rank_id", foreignKey = @ForeignKey(name = "fk_problems_problem_rank"))
    private ProblemRank rank;

    public void update(String title, String content, Long memoryLimit, Double timeLimit, ProblemRank rank) {
        this.title = title;
        this.content = content;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
        this.rank = rank;
    }
}
