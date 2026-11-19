package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class SelfGroupInvitationException extends ZukBamException {
    public SelfGroupInvitationException() {
        super(GroupErrorCode.SELF_INVITATION);
    }
}
