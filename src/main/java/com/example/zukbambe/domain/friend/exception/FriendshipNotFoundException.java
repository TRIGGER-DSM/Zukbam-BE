package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class FriendshipNotFoundException extends ZukBamException {
    public FriendshipNotFoundException() {
        super(FriendErrorCode.FRIENDSHIP_NOT_FOUND);
    }
}
