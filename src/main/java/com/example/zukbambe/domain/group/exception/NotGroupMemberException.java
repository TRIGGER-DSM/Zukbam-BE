package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class NotGroupMemberException extends ZukBamException {
    public NotGroupMemberException() {
        super(GroupErrorCode.NOT_GROUP_MEMBER);
    }
}
