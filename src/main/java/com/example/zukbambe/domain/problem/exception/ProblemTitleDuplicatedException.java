package com.example.zukbambe.domain.problem.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ProblemTitleDuplicatedException extends ZukBamException {
    public ProblemTitleDuplicatedException() {
        super(ProblemErrorCode.PROBLEM_TITLE_DUPLICATED);
    }
}
