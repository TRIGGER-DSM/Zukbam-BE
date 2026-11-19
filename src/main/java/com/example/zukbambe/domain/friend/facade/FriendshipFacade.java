package com.example.zukbambe.domain.friend.facade;

import com.example.zukbambe.domain.friend.domain.Friendship;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.exception.FriendRequestNotFoundException;
import com.example.zukbambe.domain.friend.exception.FriendshipNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipFacade {

    private final FriendshipRepository friendshipRepository;

    // 내가 받은 대기 중인 요청이 아니면 존재 여부를 노출하지 않고 404로 응답한다
    public Friendship getReceivedPendingRequest(Long friendshipId, Long userId) {
        return friendshipRepository.findById(friendshipId)
            .filter(friendship -> friendship.isPending() && friendship.isReceiver(userId))
            .orElseThrow(FriendRequestNotFoundException::new);
    }

    public Friendship getMyFriendship(Long friendshipId, Long userId) {
        return friendshipRepository.findById(friendshipId)
            .filter(friendship -> friendship.isAccepted() && friendship.isParticipant(userId))
            .orElseThrow(FriendshipNotFoundException::new);
    }
}
