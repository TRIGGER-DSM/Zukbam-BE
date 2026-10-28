package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GroupDetailResponse(
    Long groupId,
    String title,
    String introduce,
    LocalDateTime createdAt,
    List<GroupMemberResponse> members
) {
    public static GroupDetailResponse of(Group group, List<GroupMember> members) {
        return GroupDetailResponse.builder()
            .groupId(group.getGroupId())
            .title(group.getTitle())
            .introduce(group.getIntroduce())
            .createdAt(group.getCreatedAt())
            .members(members.stream().map(GroupMemberResponse::from).toList())
            .build();
    }
}
