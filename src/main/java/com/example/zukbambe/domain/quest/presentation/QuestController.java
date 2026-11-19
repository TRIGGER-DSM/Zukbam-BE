package com.example.zukbambe.domain.quest.presentation;

import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import com.example.zukbambe.domain.quest.presentation.dto.response.QuestListResponse;
import com.example.zukbambe.domain.quest.service.QueryTodayQuestListService;
import com.example.zukbambe.domain.quest.service.ReceiveQuestRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Quest")
@RestController
@RequestMapping("/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QueryTodayQuestListService queryTodayQuestListService;
    private final ReceiveQuestRewardService receiveQuestRewardService;

    @Operation(summary = "오늘의 퀘스트 조회", description = "오늘 처음 맞힌 문제 기준으로 바로 집계한다. resetAt은 다음 날 0시다.")
    @GetMapping("/today")
    public QuestListResponse getTodayQuests() {
        return queryTodayQuestListService.execute();
    }

    @Operation(summary = "퀘스트 보상 받기", description = "완료한 오늘의 퀘스트의 난이도에 따라 XP를 지급한다. 퀘스트마다 하루에 한 번만 받을 수 있다.")
    @PostMapping("/{quest}/reward")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void receiveQuestReward(@PathVariable DailyQuest quest) {
        receiveQuestRewardService.execute(quest);
    }
}
