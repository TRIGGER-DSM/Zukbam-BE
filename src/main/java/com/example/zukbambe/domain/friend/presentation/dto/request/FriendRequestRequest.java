package com.example.zukbambe.domain.friend.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record FriendRequestRequest(
    @NotNull(message = "친구 요청을 받을 유저 ID는 필수입니다.")
    Long receiverId
) {
}
