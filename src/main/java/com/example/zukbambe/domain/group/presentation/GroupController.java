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

    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody @Valid GroupRequest request) {
        Long groupId = createGroupService.execute(request);
        return ResponseEntity.created(URI.create("/groups/" + groupId)).build();
    }

    @GetMapping("/me")
    public GroupListResponse getMyGroups() {
        return queryMyGroupsService.execute();
    }

    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroup(@PathVariable Long groupId) {
        return queryGroupDetailService.execute(groupId);
    }

    @PutMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGroup(@PathVariable Long groupId, @RequestBody @Valid GroupRequest request) {
        updateGroupService.execute(groupId, request);
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long groupId) {
        deleteGroupService.execute(groupId);
    }

    @PatchMapping("/{groupId}/owner")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferGroupOwner(@PathVariable Long groupId, @RequestBody @Valid GroupOwnerTransferRequest request) {
        transferGroupOwnerService.execute(groupId, request.newOwnerId());
    }

    @DeleteMapping("/{groupId}/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGroup(@PathVariable Long groupId) {
        leaveGroupService.execute(groupId);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGroupMember(@PathVariable Long groupId, @PathVariable Long userId) {
        removeGroupMemberService.execute(groupId, userId);
    }
}
