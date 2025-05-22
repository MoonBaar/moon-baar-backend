package com.moonbaar.domain.like.exception;

import com.moonbaar.common.exception.BusinessException;

public class AlreadyLikedEventException extends BusinessException {

    public AlreadyLikedEventException() {
        super(LikeErrorCode.ALREADY_LIKED_EVENT);
    }
}
