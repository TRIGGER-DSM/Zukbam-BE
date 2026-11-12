package com.example.zukbambe.domain.notice.presentation.dto.response;

import com.example.zukbambe.domain.notice.domain.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    String authorName,
    LocalDateTime createdAt
) {
    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
            .noticeId(notice.getNoticeId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .authorName(notice.getAuthor().getName())
            .createdAt(notice.getCreatedAt())
            .build();
    }
}
