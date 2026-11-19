package com.example.zukbambe.domain.user.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class UserNotFoundException extends ZukBamException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
