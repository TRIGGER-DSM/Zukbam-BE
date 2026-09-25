package com.example.zukbambe.domain.streak.domain;

import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_streaks")
public class Streak {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(nullable = false, name = "current_streak")
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(nullable = false, name = "max_streak")
    private Integer maxStreak = 0;
}
