package com.moonbaar.domain.like.exception;

import com.moonbaar.common.exception.BusinessException;

public class LikeNotFoundException extends BusinessException {

    public LikeNotFoundException() {
        super(LikeErrorCode.LIKE_NOT_FOUND);
    }
}
