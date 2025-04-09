package com.moonbaar.domain.event.exeption;

import com.moonbaar.common.exception.BusinessException;

public class EventNotFoundException extends BusinessException {

    public EventNotFoundException() {
        super(EventErrorCode.EVENT_NOT_FOUND);
    }
}
