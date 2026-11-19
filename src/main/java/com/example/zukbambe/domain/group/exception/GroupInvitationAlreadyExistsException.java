package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupInvitationAlreadyExistsException extends ZukBamException {
    public GroupInvitationAlreadyExistsException() {
        super(GroupErrorCode.INVITATION_ALREADY_EXISTS);
    }
}
