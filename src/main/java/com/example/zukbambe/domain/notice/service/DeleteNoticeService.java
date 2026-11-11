package com.example.zukbambe.domain.notice.service;

import com.example.zukbambe.domain.notice.domain.repository.NoticeRepository;
import com.example.zukbambe.domain.notice.facade.NoticeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteNoticeService {

    private final NoticeFacade noticeFacade;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void execute(Long noticeId) {
        noticeRepository.delete(noticeFacade.getNoticeById(noticeId));
    }
}
