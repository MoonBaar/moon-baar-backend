package com.moonbaar.domain.like.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record LikedEventListRequest(
        @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
        Integer page,

        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
        Integer size,

        @Pattern(regexp = "^(asc|desc)$", message = "정렬 순서는 'asc' 또는 'desc'만 가능합니다")
        String order
) {

    public LikedEventListRequest {
        if (page == null) page = 1;
        if (size == null) size = 20;
        if (order == null) order = "desc";
    }

    public static LikedEventListRequest of(Integer page, Integer size, String order) {
        return new LikedEventListRequest(page, size, order);
    }

    public Pageable toPageable() {
        Sort.Direction direction = "desc".equalsIgnoreCase(order)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
    }
}
