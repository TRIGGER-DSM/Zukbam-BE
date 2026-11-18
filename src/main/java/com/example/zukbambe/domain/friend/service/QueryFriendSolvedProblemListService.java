package com.example.zukbambe.domain.friend.service;

import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
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
    private final SolvedHistoryRepository solvedHistoryRepository;

    @Transactional(readOnly = true)
    public FriendSolvedProblemListResponse execute(int page, int size) {
        Long userId = userFacade.getCurrentUserId();
        Page<Long> problemIds = solvedHistoryRepository.findFriendSolvedProblemIds(
            userId, FriendshipStatus.ACCEPTED, PageRequest.of(page, size)
        );

        Map<Long, List<SolvedHistory>> solvedHistoriesByProblemId = problemIds.isEmpty()
            ? Map.of()
            : solvedHistoryRepository.findFriendSolvedHistories(userId, FriendshipStatus.ACCEPTED, problemIds.getContent()).stream()
                .collect(Collectors.groupingBy(history -> history.getId().getProblemId()));

        return FriendSolvedProblemListResponse.of(problemIds, solvedHistoriesByProblemId);
    }
}
