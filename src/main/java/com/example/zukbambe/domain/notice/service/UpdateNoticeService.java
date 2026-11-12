package com.example.zukbambe.domain.notice.service;

import com.example.zukbambe.domain.notice.domain.Notice;
import com.example.zukbambe.domain.notice.facade.NoticeFacade;
import com.example.zukbambe.domain.notice.presentation.dto.request.NoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateNoticeService {

    private final NoticeFacade noticeFacade;

    @Transactional
    public void execute(Long noticeId, NoticeRequest request) {
        Notice notice = noticeFacade.getNoticeById(noticeId);
        notice.update(request.title(), request.content());
    }
}
