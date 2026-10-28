package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.facade.FriendshipFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RejectFriendRequestService {

    private final UserFacade userFacade;
    private final FriendshipFacade friendshipFacade;

    @Transactional
    public void execute(Long friendshipId) {
        friendshipFacade.getReceivedPendingRequest(friendshipId, userFacade.getCurrentUserId())
            .reject();
    }
}
