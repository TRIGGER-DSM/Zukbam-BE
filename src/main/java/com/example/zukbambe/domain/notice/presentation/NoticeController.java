package com.example.zukbambe.domain.notice.presentation;

import com.example.zukbambe.domain.notice.presentation.dto.request.NoticeRequest;
import com.example.zukbambe.domain.notice.presentation.dto.response.NoticeDetailResponse;
import com.example.zukbambe.domain.notice.presentation.dto.response.NoticeListResponse;
import com.example.zukbambe.domain.notice.service.CreateNoticeService;
import com.example.zukbambe.domain.notice.service.DeleteNoticeService;
import com.example.zukbambe.domain.notice.service.QueryNoticeDetailService;
import com.example.zukbambe.domain.notice.service.QueryNoticeListService;
import com.example.zukbambe.domain.notice.service.UpdateNoticeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Notice")
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final CreateNoticeService createNoticeService;
    private final QueryNoticeListService queryNoticeListService;
    private final QueryNoticeDetailService queryNoticeDetailService;
    private final UpdateNoticeService updateNoticeService;
    private final DeleteNoticeService deleteNoticeService;

    /**
     * 공지사항 작성<p>
     * TEACHER 권한 필요. 내용은 마크다운 원문 그대로 저장한다.
     * 생성된 공지사항 경로는 Location 헤더로 반환한다.
     */
    @ApiResponse(responseCode = "201")
    @PostMapping
    public ResponseEntity<Void> createNotice(@RequestBody @Valid NoticeRequest request) {
        Long noticeId = createNoticeService.execute(request);
        URI location = URI.create("/notices/" + noticeId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 공지사항 목록 조회<p>
     * 로그인하지 않아도 볼 수 있다. 최신순으로 나오고 내용은 포함하지 않는다.
     */
    @GetMapping
    public NoticeListResponse getNotices(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryNoticeListService.execute(page, size);
    }

    /**
     * 공지사항 상세 조회<p>
     * 로그인하지 않아도 볼 수 있다. 내용은 마크다운 원문으로 내려준다.
     */
    @GetMapping("/{noticeId}")
    public NoticeDetailResponse getNotice(@PathVariable Long noticeId) {
        return queryNoticeDetailService.execute(noticeId);
    }

    /**
     * 공지사항 수정<p>
     * TEACHER 권한 필요. 다른 선생님이 쓴 공지사항도 수정할 수 있다.
     */
    @PutMapping("/{noticeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotice(
        @PathVariable Long noticeId,
        @RequestBody @Valid NoticeRequest request
    ) {
        updateNoticeService.execute(noticeId, request);
    }

    /**
     * 공지사항 삭제<p>
     * TEACHER 권한 필요. 다른 선생님이 쓴 공지사항도 삭제할 수 있다.
     */
    @DeleteMapping("/{noticeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotice(@PathVariable Long noticeId) {
        deleteNoticeService.execute(noticeId);
    }
}
