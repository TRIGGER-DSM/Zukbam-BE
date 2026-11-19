package com.example.zukbambe.domain.solution.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class SolutionNotFoundException extends ZukBamException {
    public SolutionNotFoundException() {
        super(SolutionErrorCode.SOLUTION_NOT_FOUND);
    }
}
