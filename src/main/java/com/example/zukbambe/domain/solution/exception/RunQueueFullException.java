package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class RunQueueFullException extends ZukBamException {
    public RunQueueFullException() {
        super(SolutionErrorCode.RUN_QUEUE_FULL);
    }
}
