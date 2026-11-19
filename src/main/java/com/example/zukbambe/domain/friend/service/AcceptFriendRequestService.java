package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.facade.FriendshipFacade;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AcceptFriendRequestService {

    private final UserFacade userFacade;
    private final FriendshipFacade friendshipFacade;
    private final Clock clock;

    @Transactional
    public void execute(Long friendshipId) {
        friendshipFacade.getReceivedPendingRequest(friendshipId, userFacade.getCurrentUserId())
            .accept(LocalDateTime.now(clock));
    }
}
