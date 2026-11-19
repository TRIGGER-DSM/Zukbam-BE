package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.enums.GroupRole;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.domain.repository.GroupRepository;
import com.example.zukbambe.domain.group.presentation.dto.request.GroupRequest;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateGroupService {

    private final UserFacade userFacade;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public Long execute(GroupRequest request) {
        Group group = groupRepository.save(Group.of(request.title(), request.introduce()));
        groupMemberRepository.save(GroupMember.of(userFacade.getCurrentUser(), group, GroupRole.OWNER));
        return group.getGroupId();
    }
}
