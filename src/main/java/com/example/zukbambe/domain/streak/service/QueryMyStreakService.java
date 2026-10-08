package com.example.zukbambe.domain.streak.service;

import com.example.zukbambe.domain.streak.presentation.dto.response.StreakResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryMyStreakService {

    private final UserFacade userFacade;
    private final QueryUserStreakService queryUserStreakService;

    @Transactional(readOnly = true)
    public StreakResponse execute() {
        return queryUserStreakService.execute(userFacade.getCurrentUserId());
    }
}
