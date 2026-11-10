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

    /**
     * 친구 요청 보내기<p>
     * 나 자신이나 이미 친구인 유저에게는 보낼 수 없다.
     * 둘 사이에 대기 중인 요청이 있어도 보낼 수 없다. 상대가 먼저 보낸 요청이라면 받은 친구 요청 목록에서 수락하면 된다.
     */
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendFriendRequest(@RequestBody @Valid FriendRequestRequest request) {
        sendFriendRequestService.execute(request.receiverId());
    }

    /**
     * 받은 친구 요청 목록 조회<p>
     * 대기 중인 요청만 최신순으로 보여준다.
     */
    @GetMapping("/requests/received")
    public ReceivedFriendRequestListResponse getReceivedFriendRequests(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryReceivedFriendRequestsService.execute(page, size);
    }

    /**
     * 친구 요청 수락<p>
     * 내가 받은 요청 중 대기 중인 것만 수락할 수 있다.
     */
    @PatchMapping("/requests/{friendshipId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptFriendRequest(@PathVariable Long friendshipId) {
        acceptFriendRequestService.execute(friendshipId);
    }

    /**
     * 친구 요청 거절<p>
     * 내가 받은 요청 중 대기 중인 것만 거절할 수 있다. 거절해도 나중에 다시 요청을 주고받을 수 있다.
     */
    @PatchMapping("/requests/{friendshipId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectFriendRequest(@PathVariable Long friendshipId) {
        rejectFriendRequestService.execute(friendshipId);
    }

    /**
     * 친구 목록 조회<p>
     * 최근에 친구가 된 순서로 보여준다.
     */
    @GetMapping
    public FriendListResponse getFriends(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryFriendListService.execute(page, size);
    }

    /**
     * 친구 삭제<p>
     * 두 사람 중 누구든 삭제할 수 있다. 삭제하면 상대 친구 목록에서도 빠진다.
     */
    @DeleteMapping("/{friendshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long friendshipId) {
        deleteFriendService.execute(friendshipId);
    }
}
