package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupMemberNotFoundException extends ZukBamException {
    public GroupMemberNotFoundException() {
        super(GroupErrorCode.GROUP_MEMBER_NOT_FOUND);
    }
}
