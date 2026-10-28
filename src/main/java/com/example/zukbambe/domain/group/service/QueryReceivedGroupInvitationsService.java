package com.example.zukbambe.domain.group.service;

import com.example.zukbambe.domain.group.domain.enums.InvitationStatus;
import com.example.zukbambe.domain.group.domain.repository.GroupInvitationRepository;
import com.example.zukbambe.domain.group.presentation.dto.response.ReceivedGroupInvitationListResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueryReceivedGroupInvitationsService {

    private final UserFacade userFacade;
    private final GroupInvitationRepository groupInvitationRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public ReceivedGroupInvitationListResponse execute() {
        // 만료 시간이 지난 초대는 상태가 PENDING이어도 보여주지 않는다
        return ReceivedGroupInvitationListResponse.from(
            groupInvitationRepository.findAllByInviteeAndStatusAndExpiresAtAfterOrderByCreatedAtDesc(
                userFacade.getCurrentUser(),
                InvitationStatus.PENDING,
                LocalDateTime.now(clock)
            )
        );
    }
}
