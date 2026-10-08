package com.example.zukbambe.domain.streak.service;

import com.example.zukbambe.domain.streak.presentation.dto.response.GrassResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueryMyGrassService {

    private final UserFacade userFacade;
    private final QueryUserGrassService queryUserGrassService;

    @Transactional(readOnly = true)
    public GrassResponse execute(LocalDate from, LocalDate to) {
        return queryUserGrassService.execute(userFacade.getCurrentUserId(), from, to);
    }
}
