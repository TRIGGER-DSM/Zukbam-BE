package com.example.zukbambe.global.security.jwt.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class UnauthorizedException extends ZukBamException {
    public UnauthorizedException() {
        super(SecurityErrorCode.UNAUTHORIZED);
    }
}
