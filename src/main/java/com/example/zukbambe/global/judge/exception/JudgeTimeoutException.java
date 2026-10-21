package com.example.zukbambe.global.judge.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class JudgeTimeoutException extends ZukBamException {
    public JudgeTimeoutException() {
        super(JudgeErrorCode.JUDGE_TIMEOUT);
    }
}
