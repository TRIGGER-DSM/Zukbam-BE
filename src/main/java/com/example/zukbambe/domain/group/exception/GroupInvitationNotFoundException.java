package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupInvitationNotFoundException extends ZukBamException {
    public GroupInvitationNotFoundException() {
        super(GroupErrorCode.INVITATION_NOT_FOUND);
    }
}
