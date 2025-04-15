package com.moonbaar.domain.user.exception;

import com.moonbaar.common.exception.BusinessException;

public class UserAccessDeniedException extends BusinessException {
    public UserAccessDeniedException() {
        super(UserErrorCode.ACCESS_DENIED);
    }

}
