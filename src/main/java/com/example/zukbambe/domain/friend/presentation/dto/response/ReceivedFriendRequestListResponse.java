package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.friend.domain.Friendship;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ReceivedFriendRequestListResponse(
    List<ReceivedFriendRequestResponse> requests,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static ReceivedFriendRequestListResponse from(Page<Friendship> page) {
        return ReceivedFriendRequestListResponse.builder()
            .requests(page.getContent().stream().map(ReceivedFriendRequestResponse::from).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
