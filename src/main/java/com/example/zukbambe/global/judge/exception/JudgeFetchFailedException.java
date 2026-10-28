package com.example.zukbambe.global.judge.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class JudgeFetchFailedException extends ZukBamException {
    public JudgeFetchFailedException() {
        super(JudgeErrorCode.JUDGE_FETCH_FAILED);
    }
}
