package com.example.zukbambe.domain.solution.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_solutions")
@EntityListeners(AuditingEntityListener.class)
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solutionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "memory_used")
    private Long memoryUsed;

    @Column(name = "time")
    private Double time;

    @Column(columnDefinition = "TEXT", nullable = false, name = "code")
    private String code;

    @Column(nullable = false, name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "submitted_at")
    private LocalDateTime submittedAt;

    public static Solution of(User user, Problem problem, Language language, String code) {
        return Solution.builder()
            .user(user)
            .problem(problem)
            .status(Status.PENDING)
            .language(language)
            .code(code)
            .build();
    }
}
