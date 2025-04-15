package com.moonbaar.domain.like.exception;

import com.moonbaar.common.exception.BusinessException;

public class InvalidLikeParamsException extends BusinessException {

    public InvalidLikeParamsException() {
        super(LikeErrorCode.INVALID_LIKE_PARAMS);
    }
}
