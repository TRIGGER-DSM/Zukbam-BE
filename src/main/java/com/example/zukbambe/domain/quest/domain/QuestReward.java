package com.example.zukbambe.domain.quest.domain;

import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(
    name = "tbl_quest_rewards",
    uniqueConstraints = @UniqueConstraint(name = "uk_quest_rewards_user_date_quest", columnNames = {"user_id", "quest_date", "quest"})
)
public class QuestReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questRewardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "quest_date")
    private LocalDate questDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "quest")
    private DailyQuest quest;

    @Column(nullable = false, name = "xp")
    private Long xp;

    @Column(nullable = false, name = "rewarded_at")
    private LocalDateTime rewardedAt;
}
