package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.enums.GroupRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GroupMemberResponse(
    Long userId,
    String name,
    GroupRole role,
    LocalDateTime joinedAt
) {
    public static GroupMemberResponse from(GroupMember member) {
        return GroupMemberResponse.builder()
            .userId(member.getUser().getUserId())
            .name(member.getUser().getName())
            .role(member.getRole())
            .joinedAt(member.getJoinedAt())
            .build();
    }
}
