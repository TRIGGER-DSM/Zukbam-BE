package com.example.zukbambe.global.security.jwt.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class InvalidTokenException extends ZukBamException {
    public InvalidTokenException() {
        super(SecurityErrorCode.INVALID_TOKEN);
    }
}
