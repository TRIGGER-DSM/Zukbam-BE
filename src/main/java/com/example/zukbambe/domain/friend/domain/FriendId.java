package com.example.zukbambe.domain.friend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class FriendId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "friend_id")
    private Long friendId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendId that)) return false;
        return Objects.equals(userId, that.userId)
                && Objects.equals(friendId, that.friendId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
