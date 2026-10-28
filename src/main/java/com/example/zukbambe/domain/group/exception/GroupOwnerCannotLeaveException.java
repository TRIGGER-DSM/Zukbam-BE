package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupOwnerCannotLeaveException extends ZukBamException {
    public GroupOwnerCannotLeaveException() {
        super(GroupErrorCode.GROUP_OWNER_CANNOT_LEAVE);
    }
}
