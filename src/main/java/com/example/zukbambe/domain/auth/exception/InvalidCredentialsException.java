package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class InvalidCredentialsException extends ZukBamException {
    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS);
    }
}
