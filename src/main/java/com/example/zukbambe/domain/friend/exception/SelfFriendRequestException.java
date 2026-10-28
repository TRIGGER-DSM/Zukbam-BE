package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class SelfFriendRequestException extends ZukBamException {
    public SelfFriendRequestException() {
        super(FriendErrorCode.SELF_FRIEND_REQUEST);
    }
}
