package com.example.zukbambe.global.judge.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class JudgeSubmitFailedException extends ZukBamException {
    public JudgeSubmitFailedException(Object response) {
        super(JudgeErrorCode.JUDGE_SUBMIT_FAILED, JudgeErrorCode.JUDGE_SUBMIT_FAILED.getMessage() + " " + response);
    }
}
