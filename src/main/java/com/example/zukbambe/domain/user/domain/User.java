package com.example.zukbambe.domain.user.domain;

import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.user.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 320, nullable = false, unique = true, name = "email")
    @Email
    private String email;

    @Column(length = 60, nullable = false, name = "password")
    private String password;

    @Column(length = 255, nullable = false, name = "name")
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "CHAR(15)", nullable = false, name = "phone_number")
    private String phoneNumber;

    @Builder.Default
    @Column(nullable = false, name = "score")
    private Long score = 0L;

    @Column(nullable = false, name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_rank_id", nullable = false)
    private UserRank rank;

}
