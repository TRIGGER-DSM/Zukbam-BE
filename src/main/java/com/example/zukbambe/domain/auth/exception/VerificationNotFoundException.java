package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class VerificationNotFoundException extends ZukBamException {
    public VerificationNotFoundException() {
        super(AuthErrorCode.VERIFICATION_NOT_FOUND);
    }
}
