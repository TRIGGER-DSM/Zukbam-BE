package com.example.zukbambe.domain.friend.domain;

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
@Table(name = "tbl_friends")
@EntityListeners(AuditingEntityListener.class)
public class Friend {

    @EmbeddedId
    private FriendId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("friendId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    public static Friend of(User user, User friend) {
        return Friend.builder()
                .id(new FriendId(user.getUserId(), friend.getUserId()))
                .user(user)
                .friend(friend)
                .build();
    }
}
