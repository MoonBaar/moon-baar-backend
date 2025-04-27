package com.moonbaar.domain.badge.exception;

import com.moonbaar.common.exception.BusinessException;

public class BadgeNotFoundException extends BusinessException {
    public BadgeNotFoundException() {
        super(BadgeErrorCode.BADGE_NOT_FOUND);
    }
}
