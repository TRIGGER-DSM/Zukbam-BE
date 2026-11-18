package com.example.zukbambe.domain.category.exception;

import com.example.zukbambe.global.error.exception.ZukBamException;

public class CategoryNotFoundException extends ZukBamException {
    public CategoryNotFoundException() {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }
}
