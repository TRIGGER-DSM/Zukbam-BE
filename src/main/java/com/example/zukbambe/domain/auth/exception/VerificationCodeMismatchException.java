package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class VerificationCodeMismatchException extends ZukBamException {
    public VerificationCodeMismatchException() {
        super(AuthErrorCode.VERIFICATION_CODE_MISMATCH);
    }
}
