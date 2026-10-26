package com.example.zukbambe.domain.friend.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record FriendRequestRequest(
    @NotNull
    Long receiverId
) { }
