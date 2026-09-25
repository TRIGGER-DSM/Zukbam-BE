package com.example.zukbambe.domain.solved_history.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_solved_histories")
@EntityListeners(AuditingEntityListener.class)
public class SolvedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solvedHistoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "solved_time")
    private LocalDateTime solvedDate;

}
