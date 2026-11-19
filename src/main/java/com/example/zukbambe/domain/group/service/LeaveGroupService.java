package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.exception.GroupOwnerCannotLeaveException;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveGroupService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void execute(Long groupId) {
        GroupMember member = groupFacade.getMember(groupFacade.getGroupById(groupId), userFacade.getCurrentUserId());
        // 그룹장이 나가면 그룹장 없는 그룹이 되므로 그룹장을 위임한 뒤 탈퇴하거나 그룹을 삭제한다
        if (member.isOwner()) {
            throw new GroupOwnerCannotLeaveException();
        }
        groupMemberRepository.delete(member);
    }
}
