package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.BusinessException;

public class InvalidLocationException extends BusinessException {

    public InvalidLocationException() {
        super(VisitErroCode.INVALID_LOCATION);
    }
}
