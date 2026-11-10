package com.example.zukbambe.domain.group.presentation;

import com.example.zukbambe.domain.group.presentation.dto.request.GroupInvitationRequest;
import com.example.zukbambe.domain.group.presentation.dto.response.ReceivedGroupInvitationListResponse;
import com.example.zukbambe.domain.group.service.AcceptGroupInvitationService;
import com.example.zukbambe.domain.group.service.CancelGroupInvitationService;
import com.example.zukbambe.domain.group.service.QueryReceivedGroupInvitationsService;
import com.example.zukbambe.domain.group.service.RejectGroupInvitationService;
import com.example.zukbambe.domain.group.service.SendGroupInvitationService;
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

    /**
     * 그룹 초대 보내기<p>
     * 그룹 멤버면 누구나 초대할 수 있다. 나 자신이나 이미 멤버인 유저는 초대할 수 없다.
     * 같은 유저에게 보낸 초대가 아직 대기 중이면 다시 보낼 수 없다. 초대는 7일 동안 유효하다.
     */
    @PostMapping("/{groupId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendGroupInvitation(@PathVariable Long groupId, @RequestBody @Valid GroupInvitationRequest request) {
        sendGroupInvitationService.execute(groupId, request.inviteeId());
    }

    /**
     * 받은 그룹 초대 목록 조회<p>
     * 대기 중인 초대만 최신순으로 보여준다. 만료된 초대는 빠진다.
     */
    @GetMapping("/invitations/received")
    public ReceivedGroupInvitationListResponse getReceivedGroupInvitations() {
        return queryReceivedGroupInvitationsService.execute();
    }

    /**
     * 그룹 초대 수락<p>
     * 내가 받은 초대 중 대기 중인 것만 수락할 수 있다. 수락하면 일반 멤버로 가입된다.
     * 만료된 초대는 수락할 수 없다.
     */
    @PatchMapping("/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptGroupInvitation(@PathVariable Long invitationId) {
        acceptGroupInvitationService.execute(invitationId);
    }

    /**
     * 그룹 초대 거절<p>
     * 내가 받은 초대 중 대기 중인 것만 거절할 수 있다.
     */
    @PatchMapping("/invitations/{invitationId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectGroupInvitation(@PathVariable Long invitationId) {
        rejectGroupInvitationService.execute(invitationId);
    }

    /**
     * 그룹 초대 취소<p>
     * 초대를 보낸 사람만 취소할 수 있고, 대기 중인 초대만 취소된다.
     */
    @PatchMapping("/invitations/{invitationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelGroupInvitation(@PathVariable Long invitationId) {
        cancelGroupInvitationService.execute(invitationId);
    }
}
