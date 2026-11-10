package com.example.zukbambe.domain.xp.service;

import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecalculateMyXpService {

    private final UserFacade userFacade;
    private final UserRepository userRepository;

    @Transactional
    public void execute() {
        userRepository.recalculateXp(userFacade.getCurrentUserId());
    }
}
