package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.GroupMemberId;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.exception.GroupMemberNotFoundException;
import com.example.zukbambe.domain.group.exception.GroupOwnerCannotLeaveException;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveGroupMemberService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void execute(Long groupId, Long userId) {
        Group group = groupFacade.getGroupById(groupId);
        groupFacade.getOwner(group, userFacade.getCurrentUserId());

        GroupMember target = groupMemberRepository.findById(new GroupMemberId(userId, groupId))
            .orElseThrow(GroupMemberNotFoundException::new);
        if (target.isOwner()) {
            throw new GroupOwnerCannotLeaveException();
        }
        groupMemberRepository.delete(target);
    }
}
