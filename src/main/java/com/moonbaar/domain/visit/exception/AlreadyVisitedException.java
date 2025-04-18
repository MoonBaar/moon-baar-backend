package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.BusinessException;

public class AlreadyVisitedException extends BusinessException {

    public AlreadyVisitedException() {
        super(VisitErroCode.ALREADY_VISITED);
    }
}
