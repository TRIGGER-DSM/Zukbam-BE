package com.example.zukbambe.domain.quest.service;

import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.quest.domain.repository.QuestRewardRepository;
import com.example.zukbambe.domain.quest.facade.QuestFacade;
import com.example.zukbambe.domain.quest.presentation.dto.response.QuestListResponse;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueryTodayQuestListService {

    private final UserFacade userFacade;
    private final QuestFacade questFacade;
    private final QuestRewardRepository questRewardRepository;
    private final ProblemRankRepository problemRankRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public QuestListResponse execute() {
        Long userId = userFacade.getCurrentUserId();
        LocalDate today = LocalDate.now(clock);

        return QuestListResponse.of(
            DailyQuest.of(today),
            questFacade.getActivity(userId, today),
            problemRankRepository.findAllByOrderByXpAscProblemRankIdAsc(),
            questRewardRepository.findQuestsByUserIdAndQuestDate(userId, today),
            today.plusDays(1).atStartOfDay()
        );
    }
}
