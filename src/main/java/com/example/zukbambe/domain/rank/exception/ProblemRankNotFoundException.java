package com.example.zukbambe.domain.rank.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ProblemRankNotFoundException extends ZukBamException {
    public ProblemRankNotFoundException() {
        super(RankErrorCode.PROBLEM_RANK_NOT_FOUND);
    }
}
