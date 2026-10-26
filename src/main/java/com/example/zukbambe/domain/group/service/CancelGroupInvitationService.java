package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.facade.GroupInvitationFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelGroupInvitationService {

    private final UserFacade userFacade;
    private final GroupInvitationFacade groupInvitationFacade;

    @Transactional
    public void execute(Long invitationId) {
        groupInvitationFacade.getSentPendingInvitation(invitationId, userFacade.getCurrentUserId())
            .cancel();
    }
}
