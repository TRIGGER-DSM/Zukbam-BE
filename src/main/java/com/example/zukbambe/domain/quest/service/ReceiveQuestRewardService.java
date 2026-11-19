package com.example.zukbambe.domain.quest.service;

import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.quest.domain.repository.QuestRewardRepository;
import com.example.zukbambe.domain.quest.exception.NotTodayQuestException;
import com.example.zukbambe.domain.quest.exception.QuestAlreadyRewardedException;
import com.example.zukbambe.domain.quest.exception.QuestNotCompletedException;
import com.example.zukbambe.domain.quest.facade.QuestFacade;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.domain.xp.facade.XpFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReceiveQuestRewardService {

    private final UserFacade userFacade;
    private final QuestFacade questFacade;
    private final QuestRewardRepository questRewardRepository;
    private final ProblemRankRepository problemRankRepository;
    private final XpFacade xpFacade;
    private final Clock clock;

    @Transactional
    public void execute(DailyQuest quest) {
        User user = userFacade.getCurrentUser();
        LocalDate today = LocalDate.now(clock);

        if (!DailyQuest.of(today).contains(quest)) {
            throw new NotTodayQuestException();
        }

        int current = quest.getCurrent(
            questFacade.getActivity(user.getUserId(), today),
            problemRankRepository.findAllByOrderByXpAscProblemRankIdAsc()
        );
        if (current < quest.getTarget()) {
            throw new QuestNotCompletedException();
        }

        long xp = quest.getDifficulty().getRewardXp();
        int inserted = questRewardRepository.insertIfAbsent(
            user.getUserId(), today, quest.name(), xp, LocalDateTime.now(clock)
        );
        if (inserted == 0) {
            throw new QuestAlreadyRewardedException();
        }

        xpFacade.grantForQuest(user, xp);
    }
}
