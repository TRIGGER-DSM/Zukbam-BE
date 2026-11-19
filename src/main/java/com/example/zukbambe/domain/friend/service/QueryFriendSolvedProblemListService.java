package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
import com.example.zukbambe.domain.friend.domain.repository.FriendshipRepository;
import com.example.zukbambe.domain.friend.presentation.dto.response.FriendSolvedProblemListResponse;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryFriendSolvedProblemListService {

    private final UserFacade userFacade;
    private final FriendshipRepository friendshipRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;

    @Transactional(readOnly = true)
    public FriendSolvedProblemListResponse execute(int page, int size) {
        Long userId = userFacade.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page, size);

        // 친구 목록을 한 번만 조회해서 두 쿼리가 같은 친구 목록을 보게 한다
        List<Long> friendIds = friendshipRepository.findFriendIds(userId, FriendshipStatus.ACCEPTED);
        if (friendIds.isEmpty()) {
            return FriendSolvedProblemListResponse.of(Page.empty(pageRequest), Map.of());
        }

        Page<Long> problemIds = solvedHistoryRepository.findFriendSolvedProblemIds(userId, friendIds, pageRequest);

        Map<Long, List<SolvedHistory>> solvedHistoriesByProblemId = problemIds.isEmpty()
            ? Map.of()
            : solvedHistoryRepository.findFriendSolvedHistories(friendIds, problemIds.getContent()).stream()
                .collect(Collectors.groupingBy(history -> history.getId().getProblemId()));

        return FriendSolvedProblemListResponse.of(problemIds, solvedHistoriesByProblemId);
    }
}
