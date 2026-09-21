package com.example.zukbambe.domain.solved_history.domain;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_solved_histories")
public class SolvedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solvedHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(nullable = false, name = "solved_time")
    private LocalDateTime solvedDate;

}
