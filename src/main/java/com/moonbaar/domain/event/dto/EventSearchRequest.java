package com.moonbaar.domain.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record EventSearchRequest(
        String query,
        Long categoryId,
        Long districtId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFree,

        @Pattern(regexp = "^(startDate|popularity)$", message = "정렬 기준은 'startDate' 또는 'popularity'만 가능합니다")
        String sort,

        @Pattern(regexp = "^(asc|desc)$", message = "정렬 순서는 'asc' 또는 'desc'만 가능합니다")
        String order,

        @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
        Integer page,

        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
        Integer size
) {

    public EventSearchRequest {
        if (sort == null) sort = "startDate";
        if (order == null) order = "asc";
        if (page == null) page = 1;
        if (size == null) size = 20;
    }

    public static EventSearchRequest of(
            String query, Long categoryId, Long districtId,
            LocalDate startDate, LocalDate endDate, Boolean isFree,
            String sort, String order, Integer page, Integer size
    ) {
        return new EventSearchRequest(
                query, categoryId, districtId, startDate, endDate, isFree,
                sort, order, page, size
        );
    }

    public Pageable toPageable() {
        Sort.Direction direction = "desc".equalsIgnoreCase(order)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        String sortProperty = determineSortProperty();
        return PageRequest.of(page - 1, size, Sort.by(direction, sortProperty));
    }

    private String determineSortProperty() {
        if ("popularity".equals(sort)) {
            return "id"; // 임시로 id로 정렬(인기도 정렬은 추후 방문수와 좋아요 수 기준으로 확장)
        }
        return "startDate"; // 기본값은 시작일
    }
}
