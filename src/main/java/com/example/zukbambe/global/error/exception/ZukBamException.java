package com.example.zukbambe.global.error.exception;

import lombok.Getter;

@Getter
public class ZukBamException extends RuntimeException {
    private final ErrorProperty errorCode;
    public ZukBamException(ErrorProperty errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ZukBamException(ErrorProperty errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
