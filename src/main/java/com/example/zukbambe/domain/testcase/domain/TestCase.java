package com.example.zukbambe.domain.testcase.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_testcases")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testcaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problemId;

    @Column(name = "example_read")
    private String exampleRead;

    @Column(nullable = false, name = "example_write")
    private String exampleWrite;

    @Column(nullable = false, name = "is_sample")
    private Boolean IsSample;

}
