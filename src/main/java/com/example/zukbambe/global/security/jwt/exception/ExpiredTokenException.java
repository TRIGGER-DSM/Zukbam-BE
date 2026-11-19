package com.example.zukbambe.global.security.jwt.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ExpiredTokenException extends ZukBamException {
    public ExpiredTokenException() {
        super(SecurityErrorCode.EXPIRED_TOKEN);
    }
}
