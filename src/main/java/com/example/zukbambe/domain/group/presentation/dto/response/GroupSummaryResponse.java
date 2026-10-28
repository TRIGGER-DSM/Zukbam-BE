package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.enums.GroupRole;
import lombok.Builder;

@Builder
public record GroupSummaryResponse(
    Long groupId,
    String title,
    String introduce,
    GroupRole myRole
) {
    public static GroupSummaryResponse from(GroupMember member) {
        Group group = member.getGroup();
        return GroupSummaryResponse.builder()
            .groupId(group.getGroupId())
            .title(group.getTitle())
            .introduce(group.getIntroduce())
            .myRole(member.getRole())
            .build();
    }
}
