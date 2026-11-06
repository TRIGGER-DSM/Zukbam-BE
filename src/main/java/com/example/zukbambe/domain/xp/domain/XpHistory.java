package com.example.zukbambe.domain.xp.domain;

import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(
    name = "tbl_xp_histories",
    indexes = @Index(name = "idx_xp_histories_user_created", columnList = "user_id, created_at")
)
public class XpHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long xpHistoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 보상 이벤트로 차감할 수 있어서 음수도 허용한다
    @Column(nullable = false, name = "amount")
    private Long amount;

    @Column(columnDefinition = "TEXT", nullable = false, name = "reason")
    private String reason;

    // 문제가 삭제돼도 내역은 남아야 해서 FK 없이 값만 저장한다
    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "problem_title")
    private String problemTitle;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public static XpHistory of(User user, long amount, String reason, Long problemId, String problemTitle, LocalDateTime createdAt) {
        return XpHistory.builder()
            .user(user)
            .amount(amount)
            .reason(reason)
            .problemId(problemId)
            .problemTitle(problemTitle)
            .createdAt(createdAt)
            .build();
    }
}
