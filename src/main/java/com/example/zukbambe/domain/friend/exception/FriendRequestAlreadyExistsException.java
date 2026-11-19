package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class FriendRequestAlreadyExistsException extends ZukBamException {
    public FriendRequestAlreadyExistsException() {
        super(FriendErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
    }
}
