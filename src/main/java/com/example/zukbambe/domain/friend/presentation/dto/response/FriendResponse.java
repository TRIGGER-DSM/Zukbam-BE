package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.friend.domain.Friendship;
import com.example.zukbambe.domain.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FriendResponse(
    Long friendshipId,
    Long userId,
    String name,
    LocalDateTime acceptedAt
) {
    public static FriendResponse of(Friendship friendship, Long myUserId) {
        User friend = friendship.getOther(myUserId);
        return FriendResponse.builder()
            .friendshipId(friendship.getFriendshipId())
            .userId(friend.getUserId())
            .name(friend.getName())
            .acceptedAt(friendship.getAcceptedAt())
            .build();
    }
}
