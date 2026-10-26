package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class SelfGroupOwnerTransferException extends ZukBamException {
    public SelfGroupOwnerTransferException() {
        super(GroupErrorCode.SELF_OWNER_TRANSFER);
    }
}
