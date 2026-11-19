package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.GroupMember;

import java.util.List;

public record GroupListResponse(
    List<GroupSummaryResponse> groups
) {
    public static GroupListResponse from(List<GroupMember> members) {
        return new GroupListResponse(members.stream().map(GroupSummaryResponse::from).toList());
    }
}
