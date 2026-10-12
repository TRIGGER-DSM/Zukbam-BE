package com.example.zukbambe.domain.auth.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class TeacherCodeMismatchException extends ZukBamException {
    public TeacherCodeMismatchException() {
        super(AuthErrorCode.TEACHER_CODE_MISMATCH);
    }
}
