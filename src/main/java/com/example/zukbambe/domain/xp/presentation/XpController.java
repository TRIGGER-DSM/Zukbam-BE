package com.example.zukbambe.domain.xp.presentation;

import com.example.zukbambe.domain.xp.presentation.dto.response.XpHistoryListResponse;
import com.example.zukbambe.domain.xp.service.QueryXpHistoryListService;
import com.example.zukbambe.domain.xp.service.RecalculateMyXpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Xp")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class XpController {

    private final QueryXpHistoryListService queryXpHistoryListService;
    private final RecalculateMyXpService recalculateMyXpService;

    @Operation(summary = "유저 XP 내역 조회", description = "최신순으로 조회한다.")
    @GetMapping("/{userId}/xp-histories")
    public XpHistoryListResponse getXpHistories(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryXpHistoryListService.execute(userId, page, size);
    }

    @Operation(summary = "내 XP 재계산", description = "XP 내역 합계로 내 XP와 랭크를 다시 맞춘다.")
    @PostMapping("/me/xp/recalculate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recalculateMyXp() {
        recalculateMyXpService.execute();
    }
}
