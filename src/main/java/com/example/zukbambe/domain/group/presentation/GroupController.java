package com.example.zukbambe.domain.group.presentation;

import com.example.zukbambe.domain.group.presentation.dto.request.GroupOwnerTransferRequest;
import com.example.zukbambe.domain.group.presentation.dto.request.GroupRequest;
import com.example.zukbambe.domain.group.presentation.dto.response.GroupDetailResponse;
import com.example.zukbambe.domain.group.presentation.dto.response.GroupListResponse;
import com.example.zukbambe.domain.group.service.CreateGroupService;
import com.example.zukbambe.domain.group.service.DeleteGroupService;
import com.example.zukbambe.domain.group.service.LeaveGroupService;
import com.example.zukbambe.domain.group.service.QueryGroupDetailService;
import com.example.zukbambe.domain.group.service.QueryMyGroupsService;
import com.example.zukbambe.domain.group.service.RemoveGroupMemberService;
import com.example.zukbambe.domain.group.service.TransferGroupOwnerService;
import com.example.zukbambe.domain.group.service.UpdateGroupService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Group")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final CreateGroupService createGroupService;
    private final QueryMyGroupsService queryMyGroupsService;
    private final QueryGroupDetailService queryGroupDetailService;
    private final UpdateGroupService updateGroupService;
    private final DeleteGroupService deleteGroupService;
    private final LeaveGroupService leaveGroupService;
    private final RemoveGroupMemberService removeGroupMemberService;
    private final TransferGroupOwnerService transferGroupOwnerService;

    /**
     * 그룹 생성<p>
     * 만든 사람이 그룹장이 된다. 생성된 그룹 경로는 Location 헤더로 반환한다.
     */
    @ApiResponse(responseCode = "201")
    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody @Valid GroupRequest request) {
        Long groupId = createGroupService.execute(request);
        return ResponseEntity.created(URI.create("/groups/" + groupId)).build();
    }

    /**
     * 내 그룹 목록 조회<p>
     * 내가 가입한 그룹을 최근에 가입한 순서로 보여준다.
     */
    @GetMapping("/me")
    public GroupListResponse getMyGroups() {
        return queryMyGroupsService.execute();
    }

    /**
     * 그룹 상세 조회<p>
     * 그룹 멤버만 볼 수 있다. 멤버 목록은 가입한 순서대로 나온다.
     */
    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroup(@PathVariable Long groupId) {
        return queryGroupDetailService.execute(groupId);
    }

    /**
     * 그룹 수정<p>
     * 그룹장만 수정할 수 있다.
     */
    @PutMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGroup(@PathVariable Long groupId, @RequestBody @Valid GroupRequest request) {
        updateGroupService.execute(groupId, request);
    }

    /**
     * 그룹 삭제<p>
     * 그룹장만 삭제할 수 있다. 그룹의 멤버와 초대도 같이 지워진다.
     */
    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long groupId) {
        deleteGroupService.execute(groupId);
    }

    /**
     * 그룹장 위임<p>
     * 그룹장만 할 수 있고, 같은 그룹 멤버에게만 넘길 수 있다.
     * 넘겨준 그룹장은 일반 멤버가 된다.
     */
    @PatchMapping("/{groupId}/owner")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferGroupOwner(@PathVariable Long groupId, @RequestBody @Valid GroupOwnerTransferRequest request) {
        transferGroupOwnerService.execute(groupId, request.newOwnerId());
    }

    /**
     * 그룹 탈퇴<p>
     * 그룹장은 탈퇴할 수 없다. 다른 멤버에게 그룹장을 위임한 뒤 탈퇴하거나 그룹을 삭제해야 한다.
     */
    @DeleteMapping("/{groupId}/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGroup(@PathVariable Long groupId) {
        leaveGroupService.execute(groupId);
    }

    /**
     * 그룹 멤버 내보내기<p>
     * 그룹장만 할 수 있다. 그룹장 본인은 내보낼 수 없다.
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGroupMember(@PathVariable Long groupId, @PathVariable Long userId) {
        removeGroupMemberService.execute(groupId, userId);
    }
}
