package com.moonbaar.domain.visit.exception;

import com.moonbaar.common.exception.BusinessException;

public class RecentlyVisitedException extends BusinessException {

    public RecentlyVisitedException() {
        super(VisitErrorCode.RECENTLY_VISITED);
    }
}
