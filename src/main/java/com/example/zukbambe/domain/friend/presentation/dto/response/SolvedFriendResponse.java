package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SolvedFriendResponse(
    Long userId,
    String name,
    String rankName,
    LocalDateTime solvedAt
) {
    public static SolvedFriendResponse from(SolvedHistory solvedHistory) {
        User friend = solvedHistory.getUser();
        return SolvedFriendResponse.builder()
            .userId(friend.getUserId())
            .name(friend.getName())
            .rankName(friend.getRank().getName())
            .solvedAt(solvedHistory.getSolvedDate())
            .build();
    }
}
