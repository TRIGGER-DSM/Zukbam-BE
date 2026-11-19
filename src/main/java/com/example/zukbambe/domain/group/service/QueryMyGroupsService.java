package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.repository.GroupMemberRepository;
import com.example.zukbambe.domain.group.presentation.dto.response.GroupListResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryMyGroupsService {

    private final UserFacade userFacade;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public GroupListResponse execute() {
        return GroupListResponse.from(
            groupMemberRepository.findAllByUserOrderByJoinedAtDesc(userFacade.getCurrentUser())
        );
    }
}
