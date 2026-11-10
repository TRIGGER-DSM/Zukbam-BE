package com.example.zukbambe.domain.quest.presentation;

import com.example.zukbambe.domain.quest.presentation.dto.response.QuestListResponse;
import com.example.zukbambe.domain.quest.service.QueryTodayQuestListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Quest")
@RestController
@RequestMapping("/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QueryTodayQuestListService queryTodayQuestListService;

    @Operation(summary = "오늘의 퀘스트 조회", description = "오늘 처음 맞힌 문제 기준으로 바로 집계한다. resetAt은 다음 날 0시다.")
    @GetMapping("/today")
    public QuestListResponse getTodayQuests() {
        return queryTodayQuestListService.execute();
    }
}
