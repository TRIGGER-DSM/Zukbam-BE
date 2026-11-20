package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class TooManyRunsException extends ZukBamException {
    public TooManyRunsException() {
        super(SolutionErrorCode.TOO_MANY_RUNS);
    }
}
