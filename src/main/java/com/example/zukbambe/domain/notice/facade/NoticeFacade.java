package com.example.zukbambe.domain.notice.facade;

import com.example.zukbambe.domain.notice.domain.Notice;
import com.example.zukbambe.domain.notice.domain.repository.NoticeRepository;
import com.example.zukbambe.domain.notice.exception.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeFacade {

    private final NoticeRepository noticeRepository;

    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
            .orElseThrow(NoticeNotFoundException::new);
    }
}
