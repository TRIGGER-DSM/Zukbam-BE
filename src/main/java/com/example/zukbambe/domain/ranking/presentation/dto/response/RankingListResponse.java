package com.example.zukbambe.domain.ranking.presentation.dto.response;

import com.example.zukbambe.domain.ranking.domain.repository.RankingRow;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record RankingListResponse(
    List<RankingResponse> rankings,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static RankingListResponse from(Page<RankingRow> page) {
        return RankingListResponse.builder()
            .rankings(page.getContent().stream().map(RankingResponse::from).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
