package com.example.zukbambe.domain.friend.domain;

import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
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
@Table(name = "tbl_friendships")
@EntityListeners(AuditingEntityListener.class)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendshipId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    public static Friendship of(User requester, User receiver) {
        return Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();
    }

    public void accept(LocalDateTime acceptedAt) {
        this.status = FriendshipStatus.ACCEPTED;
        this.acceptedAt = acceptedAt;
    }

    public void reject() {
        this.status = FriendshipStatus.REJECTED;
    }

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isReceiver(Long userId) {
        return receiver.getUserId().equals(userId);
    }

    public boolean isParticipant(Long userId) {
        return requester.getUserId().equals(userId) || isReceiver(userId);
    }

    public User getOther(Long userId) {
        return requester.getUserId().equals(userId) ? receiver : requester;
    }
}
