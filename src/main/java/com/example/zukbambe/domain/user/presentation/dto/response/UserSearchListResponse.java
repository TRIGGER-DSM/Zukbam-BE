package com.example.zukbambe.domain.user.presentation.dto.response;

import com.example.zukbambe.domain.user.domain.User;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Builder
public record UserSearchListResponse(
    List<UserSearchResponse> users,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static UserSearchListResponse of(Page<User> page, Map<Long, StudentInfoResponse> studentByUserId) {
        return UserSearchListResponse.builder()
            .users(page.getContent().stream()
                .map(user -> UserSearchResponse.of(user, studentByUserId.get(user.getUserId())))
                .toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
