package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.friend.domain.Friendship;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record FriendListResponse(
    List<FriendResponse> friends,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static FriendListResponse of(Page<Friendship> page, Long myUserId) {
        return FriendListResponse.builder()
            .friends(page.getContent().stream().map(friendship -> FriendResponse.of(friendship, myUserId)).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
