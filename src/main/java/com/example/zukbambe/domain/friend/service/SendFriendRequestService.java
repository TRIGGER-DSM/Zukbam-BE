package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.Friendship;
import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.exception.AlreadyFriendsException;
import com.example.zukbambe.domain.friend.exception.FriendRequestAlreadyExistsException;
import com.example.zukbambe.domain.friend.exception.SelfFriendRequestException;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendFriendRequestService {

    private final UserFacade userFacade;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void execute(Long receiverId) {
        User requester = userFacade.getCurrentUser();
        if (requester.getUserId().equals(receiverId)) {
            throw new SelfFriendRequestException();
        }
        User receiver = userFacade.getUserById(receiverId);

        if (friendshipRepository.existsBetween(requester, receiver, FriendshipStatus.ACCEPTED)) {
            throw new AlreadyFriendsException();
        }
        // 상대가 먼저 보낸 요청이 대기 중이어도 막는다. 받은 요청 목록에서 수락하면 된다
        if (friendshipRepository.existsBetween(requester, receiver, FriendshipStatus.PENDING)) {
            throw new FriendRequestAlreadyExistsException();
        }

        // 동시에 들어온 요청은 위 검사를 함께 통과할 수 있어서 uk_friendships_active_pair 인덱스로 한 번 더 막는다
        try {
            friendshipRepository.saveAndFlush(Friendship.of(requester, receiver));
        } catch (DataIntegrityViolationException e) {
            throw new FriendRequestAlreadyExistsException();
        }
    }
}
