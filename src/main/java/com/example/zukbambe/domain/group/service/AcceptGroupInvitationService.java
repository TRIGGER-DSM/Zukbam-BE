package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupInvitation;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.enums.GroupRole;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.exception.AlreadyGroupMemberException;
import com.example.zukbambe.domain.group.exception.GroupInvitationExpiredException;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.group.facade.GroupInvitationFacade;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AcceptGroupInvitationService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupInvitationFacade groupInvitationFacade;
    private final GroupMemberRepository groupMemberRepository;
    private final Clock clock;

    // 만료된 초대는 EXPIRED로 바꾼 상태를 저장한 뒤 예외를 던진다
    @Transactional(noRollbackFor = GroupInvitationExpiredException.class)
    public void execute(Long invitationId) {
        User user = userFacade.getCurrentUser();
        GroupInvitation invitation = groupInvitationFacade.getReceivedPendingInvitation(invitationId, user.getUserId());

        if (invitation.isExpired(LocalDateTime.now(clock))) {
            invitation.expire();
            throw new GroupInvitationExpiredException();
        }

        Group group = invitation.getGroup();
        if (groupFacade.isMember(group, user.getUserId())) {
            throw new AlreadyGroupMemberException();
        }

        invitation.accept();
        // 같은 초대를 동시에 수락하면 둘 다 위 검사를 통과하므로 멤버 PK 중복으로 막는다
        try {
            groupMemberRepository.saveAndFlush(GroupMember.of(user, group, GroupRole.MEMBER));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyGroupMemberException();
        }
    }
}
