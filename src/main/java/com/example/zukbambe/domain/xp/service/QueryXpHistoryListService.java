package com.example.zukbambe.domain.xp.service;

import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import com.example.zukbambe.domain.xp.presentation.dto.response.XpHistoryListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryXpHistoryListService {

    private final UserFacade userFacade;
    private final XpHistoryRepository xpHistoryRepository;

    @Transactional(readOnly = true)
    public XpHistoryListResponse execute(Long userId, int page, int size) {
        // 없는 유저는 빈 목록이 아니라 404로 응답한다
        userFacade.getUserById(userId);

        return XpHistoryListResponse.from(
            xpHistoryRepository.findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(userId, PageRequest.of(page, size))
        );
    }
}
