package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.presentation.dto.response.FriendListResponse;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryFriendListService {

    private final UserFacade userFacade;
    private final FriendshipRepository friendshipRepository;

    @Transactional(readOnly = true)
    public FriendListResponse execute(int page, int size) {
        User user = userFacade.getCurrentUser();
        return FriendListResponse.of(
            friendshipRepository.findAllByUserAndStatus(
                user,
                FriendshipStatus.ACCEPTED,
                PageRequest.of(page, size, Sort.by("acceptedAt").descending())
            ),
            user.getUserId()
        );
    }
}
