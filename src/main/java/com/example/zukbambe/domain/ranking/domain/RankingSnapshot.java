package com.example.zukbambe.domain.ranking.domain;

import com.example.zukbambe.domain.ranking.domain.enums.RankingPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 매일 밤 다시 만드는 집계값이라 유저와 FK로 묶지 않는다. 순위는 조회할 때 매긴다
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(
    name = "tbl_ranking_snapshots",
    uniqueConstraints = @UniqueConstraint(name = "uk_ranking_snapshots_period_user", columnNames = {"period", "user_id"})
)
public class RankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankingSnapshotId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "period")
    private RankingPeriod period;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, name = "user_rank_id")
    private Long userRankId;

    @Column(nullable = false, name = "xp")
    private Long xp;

    @Column(nullable = false, name = "solved_count")
    private Long solvedCount;

    @Column(nullable = false, name = "streak")
    private Integer streak;

    @Column(nullable = false, name = "snapshot_at")
    private LocalDateTime snapshotAt;
}
