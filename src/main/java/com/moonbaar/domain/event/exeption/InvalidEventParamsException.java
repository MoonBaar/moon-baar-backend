package com.moonbaar.domain.event.exeption;

import com.moonbaar.common.exception.BusinessException;

public class InvalidEventParamsException extends BusinessException {

    public InvalidEventParamsException() {
        super(EventErrorCode.INVALID_EVENT_PARAMS);
    }
}
