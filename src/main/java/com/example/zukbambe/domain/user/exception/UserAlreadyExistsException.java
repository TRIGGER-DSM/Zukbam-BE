package com.example.zukbambe.domain.user.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class UserAlreadyExistsException extends ZukBamException {
    public UserAlreadyExistsException() {
        super(UserErrorCode.USER_ALREADY_EXISTS);
    }
}
