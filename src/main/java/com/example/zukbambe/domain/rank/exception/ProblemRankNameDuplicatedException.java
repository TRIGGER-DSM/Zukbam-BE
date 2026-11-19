package com.example.zukbambe.domain.rank.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class ProblemRankNameDuplicatedException extends ZukBamException {
    public ProblemRankNameDuplicatedException() {
        super(RankErrorCode.PROBLEM_RANK_NAME_DUPLICATED);
    }
}
