package com.example.zukbambe.domain.problem.domain;

import com.example.zukbambe.domain.problem.domain.emuns.ProblemRank;
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
    private Long timeLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "problem_rank")
    private ProblemRank rank;
}
