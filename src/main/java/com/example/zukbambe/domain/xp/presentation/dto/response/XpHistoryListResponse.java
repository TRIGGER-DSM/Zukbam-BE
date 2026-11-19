package com.example.zukbambe.domain.xp.presentation.dto.response;

import com.example.zukbambe.domain.xp.domain.XpHistory;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record XpHistoryListResponse(
    List<XpHistoryResponse> xpHistories,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static XpHistoryListResponse from(Page<XpHistory> page) {
        return XpHistoryListResponse.builder()
            .xpHistories(page.getContent().stream().map(XpHistoryResponse::from).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
