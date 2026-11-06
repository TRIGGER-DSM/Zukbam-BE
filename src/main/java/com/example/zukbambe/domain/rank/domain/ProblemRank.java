package com.example.zukbambe.domain.rank.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_problem_ranks")
public class ProblemRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemRankId;

    @Column(unique = true, nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "xp")
    private Long xp;

    @Column(nullable = false, name = "sort_order")
    private Integer sortOrder;

    public void update(String name, Long xp, Integer sortOrder) {
        this.name = name;
        this.xp = xp;
        this.sortOrder = sortOrder;
    }
}
