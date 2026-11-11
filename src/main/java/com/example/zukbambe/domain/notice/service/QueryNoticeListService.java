package com.example.zukbambe.domain.notice.service;

import com.example.zukbambe.domain.notice.domain.repository.NoticeRepository;
import com.example.zukbambe.domain.notice.presentation.dto.response.NoticeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryNoticeListService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public NoticeListResponse execute(int page, int size) {
        return NoticeListResponse.from(
            noticeRepository.findAll(PageRequest.of(page, size, Sort.by("noticeId").descending()))
        );
    }
}
