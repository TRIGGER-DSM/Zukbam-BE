package com.example.zukbambe.domain.group.presentation.dto.response;

import com.example.zukbambe.domain.group.domain.GroupInvitation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReceivedGroupInvitationResponse(
    Long invitationId,
    Long groupId,
    String groupTitle,
    Long inviterId,
    String inviterName,
    LocalDateTime invitedAt,
    LocalDateTime expiresAt
) {
    public static ReceivedGroupInvitationResponse from(GroupInvitation invitation) {
        return ReceivedGroupInvitationResponse.builder()
            .invitationId(invitation.getInvitationId())
            .groupId(invitation.getGroup().getGroupId())
            .groupTitle(invitation.getGroup().getTitle())
            .inviterId(invitation.getInviter().getUserId())
            .inviterName(invitation.getInviter().getName())
            .invitedAt(invitation.getCreatedAt())
            .expiresAt(invitation.getExpiresAt())
            .build();
    }
}
