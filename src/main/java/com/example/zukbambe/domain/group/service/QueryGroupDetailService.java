package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.group.presentation.dto.response.GroupDetailResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryGroupDetailService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public GroupDetailResponse execute(Long groupId) {
        Group group = groupFacade.getGroupById(groupId);
        // 그룹 상세는 멤버만 볼 수 있다
        groupFacade.getMember(group, userFacade.getCurrentUserId());

        return GroupDetailResponse.of(group, groupMemberRepository.findAllByGroupOrderByJoinedAtAsc(group));
    }
}
