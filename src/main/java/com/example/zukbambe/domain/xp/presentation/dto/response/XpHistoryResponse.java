package com.example.zukbambe.domain.xp.presentation.dto.response;

import com.example.zukbambe.domain.xp.domain.XpHistory;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record XpHistoryResponse(
    Long xpHistoryId,
    Long amount,
    String reason,
    Long problemId,
    String problemTitle,
    LocalDateTime createdAt
) {
    public static XpHistoryResponse from(XpHistory history) {
        return XpHistoryResponse.builder()
            .xpHistoryId(history.getXpHistoryId())
            .amount(history.getAmount())
            .reason(history.getReason())
            .problemId(history.getProblemId())
            .problemTitle(history.getProblemTitle())
            .createdAt(history.getCreatedAt())
            .build();
    }
}
