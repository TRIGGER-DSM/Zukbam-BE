package com.example.zukbambe.domain.notice.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class NoticeNotFoundException extends ZukBamException {
    public NoticeNotFoundException() {
        super(NoticeErrorCode.NOTICE_NOT_FOUND);
    }
}
