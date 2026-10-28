package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.friend.domain.Friendship;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReceivedFriendRequestResponse(
    Long friendshipId,
    Long requesterId,
    String requesterName,
    LocalDateTime requestedAt
) {
    public static ReceivedFriendRequestResponse from(Friendship friendship) {
        return ReceivedFriendRequestResponse.builder()
            .friendshipId(friendship.getFriendshipId())
            .requesterId(friendship.getRequester().getUserId())
            .requesterName(friendship.getRequester().getName())
            .requestedAt(friendship.getCreatedAt())
            .build();
    }
}
