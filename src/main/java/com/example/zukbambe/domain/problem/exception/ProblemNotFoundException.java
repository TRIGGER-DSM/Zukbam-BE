package com.example.zukbambe.domain.problem.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ProblemNotFoundException extends ZukBamException {
    public ProblemNotFoundException() {
        super(ProblemErrorCode.PROBLEM_NOT_FOUND);
    }
}
