package com.example.zukbambe.domain.rank.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class UserRankNotFoundException extends ZukBamException {
    public UserRankNotFoundException() {
        super(RankErrorCode.USER_RANK_NOT_FOUND);
    }
}
