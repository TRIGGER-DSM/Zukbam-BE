package com.example.zukbambe.global.judge.exception;

public class JudgeTimeoutException extends RuntimeException {
    public JudgeTimeoutException() {
        super("채점이 진행되지 않습니다.");
    }
}
