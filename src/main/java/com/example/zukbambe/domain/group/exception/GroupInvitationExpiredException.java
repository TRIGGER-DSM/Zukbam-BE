package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class GroupInvitationExpiredException extends ZukBamException {
    public GroupInvitationExpiredException() {
        super(GroupErrorCode.INVITATION_EXPIRED);
    }
}
