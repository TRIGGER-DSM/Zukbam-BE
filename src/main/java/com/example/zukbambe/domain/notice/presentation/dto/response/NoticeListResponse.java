package com.example.zukbambe.domain.notice.presentation.dto.response;

import com.example.zukbambe.domain.notice.domain.Notice;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record NoticeListResponse(
    List<NoticeSummaryResponse> notices,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static NoticeListResponse from(Page<Notice> page) {
        return NoticeListResponse.builder()
            .notices(page.getContent().stream().map(NoticeSummaryResponse::from).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
