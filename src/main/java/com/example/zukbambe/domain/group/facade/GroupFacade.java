package com.example.zukbambe.domain.group.facade;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.GroupMemberId;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.domain.repository.GroupRepository;
import com.example.zukbambe.domain.group.exception.GroupNotFoundException;
import com.example.zukbambe.domain.group.exception.NotGroupMemberException;
import com.example.zukbambe.domain.group.exception.NotGroupOwnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupFacade {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(GroupNotFoundException::new);
    }

    public GroupMember getMember(Group group, Long userId) {
        return groupMemberRepository.findById(new GroupMemberId(userId, group.getGroupId()))
            .orElseThrow(NotGroupMemberException::new);
    }

    public GroupMember getOwner(Group group, Long userId) {
        GroupMember member = getMember(group, userId);
        if (!member.isOwner()) {
            throw new NotGroupOwnerException();
        }
        return member;
    }

    public boolean isMember(Group group, Long userId) {
        return groupMemberRepository.existsById(new GroupMemberId(userId, group.getGroupId()));
    }
}
