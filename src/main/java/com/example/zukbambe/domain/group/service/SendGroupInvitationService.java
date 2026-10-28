package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupInvitation;
import com.example.zukbambe.domain.group.domain.enums.InvitationStatus;
import com.example.zukbambe.domain.group.domain.repository.GroupInvitationRepository;
import com.example.zukbambe.domain.group.exception.AlreadyGroupMemberException;
import com.example.zukbambe.domain.group.exception.GroupInvitationAlreadyExistsException;
import com.example.zukbambe.domain.group.exception.SelfGroupInvitationException;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendGroupInvitationService {

    private static final Duration INVITATION_TTL = Duration.ofDays(7);

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupInvitationRepository groupInvitationRepository;
    private final Clock clock;

    @Transactional
    public void execute(Long groupId, Long inviteeId) {
        User inviter = userFacade.getCurrentUser();
        Group group = groupFacade.getGroupById(groupId);
        groupFacade.getMember(group, inviter.getUserId());

        if (inviter.getUserId().equals(inviteeId)) {
            throw new SelfGroupInvitationException();
        }
        User invitee = userFacade.getUserById(inviteeId);
        if (groupFacade.isMember(group, inviteeId)) {
            throw new AlreadyGroupMemberException();
        }

        LocalDateTime now = LocalDateTime.now(clock);
        GroupInvitation pending = groupInvitationRepository
            .findByGroupAndInviteeAndStatus(group, invitee, InvitationStatus.PENDING)
            .orElse(null);
        if (pending != null) {
            if (!pending.isExpired(now)) {
                throw new GroupInvitationAlreadyExistsException();
            }
            // 만료됐지만 아직 PENDING인 초대가 uk_group_invitations_pending 인덱스에 걸리지 않도록 먼저 반영한다
            pending.expire();
            groupInvitationRepository.flush();
        }

        // 동시에 들어온 초대는 위 검사를 함께 통과할 수 있어서 인덱스로 한 번 더 막는다
        try {
            groupInvitationRepository.saveAndFlush(
                GroupInvitation.of(group, inviter, invitee, now.plus(INVITATION_TTL))
            );
        } catch (DataIntegrityViolationException e) {
            throw new GroupInvitationAlreadyExistsException();
        }
    }
}
