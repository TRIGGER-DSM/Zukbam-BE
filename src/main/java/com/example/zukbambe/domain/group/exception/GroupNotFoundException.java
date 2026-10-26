package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupNotFoundException extends ZukBamException {
    public GroupNotFoundException() {
        super(GroupErrorCode.GROUP_NOT_FOUND);
    }
}
