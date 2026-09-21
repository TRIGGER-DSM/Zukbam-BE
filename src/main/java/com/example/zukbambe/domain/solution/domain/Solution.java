package com.example.zukbambe.domain.solution.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.solution.domain.eunms.Language;
import com.example.zukbambe.domain.solution.domain.eunms.Status;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_solutions")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solutionId;

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User userId;

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id", referencedColumnName = "problemId")
    private Problem problemId;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, name = "memory_used")
    private Long memory_used;

    @Column(nullable = false, name = "time")
    private Long time;

    @Column(nullable = false, name = "code")
    private String code;

    @Column(nullable = false, name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

}
