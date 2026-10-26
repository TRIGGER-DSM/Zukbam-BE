package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.facade.GroupFacade;
import com.example.zukbambe.domain.group.presentation.dto.request.GroupRequest;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateGroupService {

    private final UserFacade userFacade;
    private final GroupFacade groupFacade;

    @Transactional
    public void execute(Long groupId, GroupRequest request) {
        Group group = groupFacade.getGroupById(groupId);
        groupFacade.getOwner(group, userFacade.getCurrentUserId());

        group.update(request.title(), request.introduce());
    }
}
