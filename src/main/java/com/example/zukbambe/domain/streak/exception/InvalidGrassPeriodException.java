package com.example.zukbambe.domain.streak.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class InvalidGrassPeriodException extends ZukBamException {
    public InvalidGrassPeriodException() {
        super(StreakErrorCode.INVALID_GRASS_PERIOD);
    }
}
