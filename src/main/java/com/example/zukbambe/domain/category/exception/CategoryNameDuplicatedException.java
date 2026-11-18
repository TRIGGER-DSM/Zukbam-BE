package com.example.zukbambe.domain.category.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class CategoryNameDuplicatedException extends ZukBamException {
    public CategoryNameDuplicatedException() {
        super(CategoryErrorCode.CATEGORY_NAME_DUPLICATED);
    }
}
