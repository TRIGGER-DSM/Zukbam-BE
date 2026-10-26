package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.repository.GroupInvitationRepository;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.domain.repository.GroupRepository;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteGroupService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    @Transactional
    public void execute(Long groupId) {
        Group group = groupFacade.getGroupById(groupId);
        groupFacade.getOwner(group, userFacade.getCurrentUserId());

        groupInvitationRepository.deleteAllByGroup(group);
        groupMemberRepository.deleteAllByGroup(group);
        groupRepository.delete(group);
    }
}
