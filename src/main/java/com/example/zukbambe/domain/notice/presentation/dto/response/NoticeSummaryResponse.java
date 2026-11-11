package com.example.zukbambe.domain.notice.presentation.dto.response;

import com.example.zukbambe.domain.notice.domain.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeSummaryResponse(
    Long noticeId,
    String title,
    String authorName,
    LocalDateTime createdAt
) {
    public static NoticeSummaryResponse from(Notice notice) {
        return NoticeSummaryResponse.builder()
            .noticeId(notice.getNoticeId())
            .title(notice.getTitle())
            .authorName(notice.getAuthor().getName())
            .createdAt(notice.getCreatedAt())
            .build();
    }
}
