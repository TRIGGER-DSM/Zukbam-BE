package com.example.zukbambe.domain.notice.service;

import com.example.zukbambe.domain.notice.domain.Notice;
import com.example.zukbambe.domain.notice.domain.repository.NoticeRepository;
import com.example.zukbambe.domain.notice.presentation.dto.request.NoticeRequest;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateNoticeService {

    private final NoticeRepository noticeRepository;
    private final UserFacade userFacade;

    @Transactional
    public Long execute(NoticeRequest request) {
        Notice notice = noticeRepository.save(
            Notice.builder()
                .title(request.title())
                .content(request.content())
                .author(userFacade.getCurrentUser())
                .build()
        );

        return notice.getNoticeId();
    }
}
