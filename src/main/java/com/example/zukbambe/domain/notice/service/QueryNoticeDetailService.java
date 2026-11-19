package com.example.zukbambe.domain.notice.service;

import com.example.zukbambe.domain.notice.facade.NoticeFacade;
import com.example.zukbambe.domain.notice.presentation.dto.response.NoticeDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryNoticeDetailService {

    private final NoticeFacade noticeFacade;

    @Transactional(readOnly = true)
    public NoticeDetailResponse execute(Long noticeId) {
        return NoticeDetailResponse.from(noticeFacade.getNoticeById(noticeId));
    }
}
