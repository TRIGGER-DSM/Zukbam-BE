package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.presentation.dto.response.ReceivedFriendRequestListResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryReceivedFriendRequestsService {

    private final UserFacade userFacade;
    private final FriendshipRepository friendshipRepository;

    @Transactional(readOnly = true)
    public ReceivedFriendRequestListResponse execute(int page, int size) {
        return ReceivedFriendRequestListResponse.from(
            friendshipRepository.findAllByReceiverAndStatus(
                userFacade.getCurrentUser(),
                FriendshipStatus.PENDING,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
            )
        );
    }
}
