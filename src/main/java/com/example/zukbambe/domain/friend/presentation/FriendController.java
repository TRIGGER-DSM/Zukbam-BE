package com.example.zukbambe.domain.friend.presentation;

import com.example.zukbambe.domain.friend.presentation.dto.request.FriendRequestRequest;
import com.example.zukbambe.domain.friend.presentation.dto.response.FriendListResponse;
import com.example.zukbambe.domain.friend.presentation.dto.response.ReceivedFriendRequestListResponse;
import com.example.zukbambe.domain.friend.service.AcceptFriendRequestService;
import com.example.zukbambe.domain.friend.service.DeleteFriendService;
import com.example.zukbambe.domain.friend.service.QueryFriendListService;
import com.example.zukbambe.domain.friend.service.QueryReceivedFriendRequestsService;
import com.example.zukbambe.domain.friend.service.RejectFriendRequestService;
import com.example.zukbambe.domain.friend.service.SendFriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Friend")
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final SendFriendRequestService sendFriendRequestService;
    private final AcceptFriendRequestService acceptFriendRequestService;
    private final RejectFriendRequestService rejectFriendRequestService;
    private final QueryReceivedFriendRequestsService queryReceivedFriendRequestsService;
    private final QueryFriendListService queryFriendListService;
    private final DeleteFriendService deleteFriendService;

    @Operation(summary = "친구 요청 보내기")
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendFriendRequest(@RequestBody @Valid FriendRequestRequest request) {
        sendFriendRequestService.execute(request.receiverId());
    }

    @Operation(summary = "받은 친구 요청 목록 조회")
    @GetMapping("/requests/received")
    public ReceivedFriendRequestListResponse getReceivedFriendRequests(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryReceivedFriendRequestsService.execute(page, size);
    }

    @Operation(summary = "친구 요청 수락")
    @PatchMapping("/requests/{friendshipId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptFriendRequest(@PathVariable Long friendshipId) {
        acceptFriendRequestService.execute(friendshipId);
    }

    @Operation(summary = "친구 요청 거절")
    @PatchMapping("/requests/{friendshipId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectFriendRequest(@PathVariable Long friendshipId) {
        rejectFriendRequestService.execute(friendshipId);
    }

    @Operation(summary = "친구 목록 조회")
    @GetMapping
    public FriendListResponse getFriends(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryFriendListService.execute(page, size);
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{friendshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long friendshipId) {
        deleteFriendService.execute(friendshipId);
    }
}
