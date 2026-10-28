package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class FriendRequestNotFoundException extends ZukBamException {
    public FriendRequestNotFoundException() {
        super(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }
}
