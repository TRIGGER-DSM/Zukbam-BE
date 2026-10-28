package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class AlreadyFriendsException extends ZukBamException {
    public AlreadyFriendsException() {
        super(FriendErrorCode.ALREADY_FRIENDS);
    }
}
