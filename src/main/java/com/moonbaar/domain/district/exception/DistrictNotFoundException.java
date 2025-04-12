package com.moonbaar.domain.district.exception;


import com.moonbaar.common.exception.BusinessException;

public class DistrictNotFoundException extends BusinessException {

    public DistrictNotFoundException() {
        super(DistrictErrorCode.DISTRICT_NOT_FOUND);
    }
}
