package com.example.zukbambe.domain.group.facade;

import com.example.zukbambe.domain.group.domain.GroupInvitation;
import com.example.zukbambe.domain.group.domain.repository.GroupInvitationRepository;
import com.example.zukbambe.domain.group.exception.GroupInvitationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupInvitationFacade {

    private final GroupInvitationRepository groupInvitationRepository;

    // 내가 받은 대기 중인 초대가 아니면 존재 여부를 노출하지 않고 404로 응답한다
    public GroupInvitation getReceivedPendingInvitation(Long invitationId, Long userId) {
        return groupInvitationRepository.findById(invitationId)
            .filter(invitation -> invitation.isPending() && invitation.isInvitee(userId))
            .orElseThrow(GroupInvitationNotFoundException::new);
    }

    public GroupInvitation getSentPendingInvitation(Long invitationId, Long userId) {
        return groupInvitationRepository.findById(invitationId)
            .filter(invitation -> invitation.isPending() && invitation.isInviter(userId))
            .orElseThrow(GroupInvitationNotFoundException::new);
    }
}
