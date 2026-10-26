package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.GroupMemberId;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.exception.GroupMemberNotFoundException;
import com.example.zukbambe.domain.group.exception.SelfGroupOwnerTransferException;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferGroupOwnerService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void execute(Long groupId, Long newOwnerId) {
        Group group = groupFacade.getGroupById(groupId);
        GroupMember owner = groupFacade.getOwner(group, userFacade.getCurrentUserId());

        if (owner.getUser().getUserId().equals(newOwnerId)) {
            throw new SelfGroupOwnerTransferException();
        }
        GroupMember newOwner = groupMemberRepository.findById(new GroupMemberId(newOwnerId, groupId))
            .orElseThrow(GroupMemberNotFoundException::new);

        owner.demoteToMember();
        newOwner.promoteToOwner();
    }
}
