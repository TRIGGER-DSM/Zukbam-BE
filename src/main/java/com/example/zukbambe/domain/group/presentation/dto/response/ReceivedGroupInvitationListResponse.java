package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.GroupInvitation;

import java.util.List;

public record ReceivedGroupInvitationListResponse(
    List<ReceivedGroupInvitationResponse> invitations
) {
    public static ReceivedGroupInvitationListResponse from(List<GroupInvitation> invitations) {
        return new ReceivedGroupInvitationListResponse(
            invitations.stream().map(ReceivedGroupInvitationResponse::from).toList()
        );
    }
}
