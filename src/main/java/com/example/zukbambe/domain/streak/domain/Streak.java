package com.example.zukbambe.domain.streak.domain;

import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    @Column(name = "last_solved_date")
    private LocalDate lastSolvedDate;

    public void recordSolved(LocalDate today) {
        // 같은 날 여러 문제를 풀어도 스트릭은 하루에 한 번만 오른다
        if (lastSolvedDate != null && !lastSolvedDate.isBefore(today)) {
            return;
        }

        this.currentStreak = isAlive(today) ? currentStreak + 1 : 1;
        this.maxStreak = Math.max(maxStreak, currentStreak);
        this.lastSolvedDate = today;
    }

    // 끊긴 스트릭은 다음 풀이 전까지 DB에 그대로 남아 있으므로 조회 시점 기준으로 계산한다
    public int getCurrentStreak(LocalDate today) {
        return isAlive(today) ? currentStreak : 0;
    }

    private boolean isAlive(LocalDate today) {
        return lastSolvedDate != null && !lastSolvedDate.isBefore(today.minusDays(1));
    }
}
