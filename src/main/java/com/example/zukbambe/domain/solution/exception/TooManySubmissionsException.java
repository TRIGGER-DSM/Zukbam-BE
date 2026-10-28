package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class TooManySubmissionsException extends ZukBamException {
    public TooManySubmissionsException() {
        super(SolutionErrorCode.TOO_MANY_SUBMISSIONS);
    }
}
