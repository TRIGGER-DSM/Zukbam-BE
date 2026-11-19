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

    // 이 랭크가 되기 위해 필요한 XP
    @Column(nullable = false, name = "xp")
    private Integer xp;
}
