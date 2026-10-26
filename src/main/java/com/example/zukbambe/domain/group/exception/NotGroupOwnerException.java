package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class NotGroupOwnerException extends ZukBamException {
    public NotGroupOwnerException() {
        super(GroupErrorCode.NOT_GROUP_OWNER);
    }
}
