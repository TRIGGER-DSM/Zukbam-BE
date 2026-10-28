package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class AlreadyGroupMemberException extends ZukBamException {
    public AlreadyGroupMemberException() {
        super(GroupErrorCode.ALREADY_GROUP_MEMBER);
    }
}
