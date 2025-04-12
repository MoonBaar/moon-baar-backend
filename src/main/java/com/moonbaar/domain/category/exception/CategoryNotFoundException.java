package com.moonbaar.domain.category.exception;

import com.moonbaar.common.exception.BusinessException;

public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException() {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }
}
