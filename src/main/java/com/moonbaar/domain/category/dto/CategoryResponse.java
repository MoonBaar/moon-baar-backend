package com.moonbaar.domain.category.dto;

import com.moonbaar.domain.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName());
    }
}
