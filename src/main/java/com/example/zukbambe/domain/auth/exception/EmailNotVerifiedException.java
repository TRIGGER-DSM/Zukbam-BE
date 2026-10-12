package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class EmailNotVerifiedException extends ZukBamException {
    public EmailNotVerifiedException() {
        super(AuthErrorCode.EMAIL_NOT_VERIFIED);
    }
}
