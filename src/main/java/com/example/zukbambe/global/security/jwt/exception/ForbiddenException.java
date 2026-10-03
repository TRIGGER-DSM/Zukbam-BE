package com.example.zukbambe.global.security.jwt.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ForbiddenException extends ZukBamException {
    public ForbiddenException() {
        super(SecurityErrorCode.FORBIDDEN);
    }
}
