package com.moonbaar.domain.visit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record VisitListRequest(
        @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
        Integer page,

        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
        Integer size,

        @Pattern(regexp = "^(all|thisMonth|thisYear)$", message = "기간은 'all', 'thisMonth', 'thisYear' 중 하나여야 합니다")
        String period
) {

    public VisitListRequest {
        if (page == null) page = 1;
        if (size == null) size = 20;
        if (period == null) period = "all";
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "visitedAt"));
    }
}
