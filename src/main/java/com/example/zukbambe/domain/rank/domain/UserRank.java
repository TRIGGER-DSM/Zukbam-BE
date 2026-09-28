package com.example.zukbambe.domain.rank.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_user_ranks")
public class UserRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRankId;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "score")
    private Integer score;
}
