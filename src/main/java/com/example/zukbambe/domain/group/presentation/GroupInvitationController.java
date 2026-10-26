package com.example.zukbambe.domain.group.presentation;

import com.example.zukbambe.domain.group.presentation.dto.request.GroupInvitationRequest;
import com.example.zukbambe.domain.group.presentation.dto.response.ReceivedGroupInvitationListResponse;
import com.example.zukbambe.domain.group.service.AcceptGroupInvitationService;
import com.example.zukbambe.domain.group.service.CancelGroupInvitationService;
import com.example.zukbambe.domain.group.service.QueryReceivedGroupInvitationsService;
import com.example.zukbambe.domain.group.service.RejectGroupInvitationService;
import com.example.zukbambe.domain.group.service.SendGroupInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group Invitation")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupInvitationController {

    private final SendGroupInvitationService sendGroupInvitationService;
    private final QueryReceivedGroupInvitationsService queryReceivedGroupInvitationsService;
    private final AcceptGroupInvitationService acceptGroupInvitationService;
    private final RejectGroupInvitationService rejectGroupInvitationService;
    private final CancelGroupInvitationService cancelGroupInvitationService;

    @Operation(summary = "그룹 초대 보내기", description = "초대는 7일간 유효하다.")
    @PostMapping("/{groupId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendGroupInvitation(@PathVariable Long groupId, @RequestBody @Valid GroupInvitationRequest request) {
        sendGroupInvitationService.execute(groupId, request.inviteeId());
    }

    @Operation(summary = "받은 그룹 초대 목록 조회", description = "만료된 초대는 제외한다.")
    @GetMapping("/invitations/received")
    public ReceivedGroupInvitationListResponse getReceivedGroupInvitations() {
        return queryReceivedGroupInvitationsService.execute();
    }

    @Operation(summary = "그룹 초대 수락")
    @PatchMapping("/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptGroupInvitation(@PathVariable Long invitationId) {
        acceptGroupInvitationService.execute(invitationId);
    }

    @Operation(summary = "그룹 초대 거절")
    @PatchMapping("/invitations/{invitationId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectGroupInvitation(@PathVariable Long invitationId) {
        rejectGroupInvitationService.execute(invitationId);
    }

    @Operation(summary = "그룹 초대 취소", description = "초대를 보낸 유저만 취소할 수 있다.")
    @PatchMapping("/invitations/{invitationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelGroupInvitation(@PathVariable Long invitationId) {
        cancelGroupInvitationService.execute(invitationId);
    }
}
