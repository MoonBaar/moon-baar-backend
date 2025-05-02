package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.BusinessException;

public class EventNotActiveException extends BusinessException {

    public EventNotActiveException() {
        super(VisitErrorCode.EVENT_NOT_ACTIVE);
    }
}
