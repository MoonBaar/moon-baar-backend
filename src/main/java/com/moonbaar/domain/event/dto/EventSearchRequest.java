package com.moonbaar.domain.event.dto;

import java.time.LocalDate;

public record EventSearchRequest(
        String query,
        Long categoryId,
        Long districtId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFree,
        String sort,
        String order,
        int page,
        int size
) {
}
