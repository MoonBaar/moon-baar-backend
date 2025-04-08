package com.moonbaar.domain.event.dto;

import java.util.List;

public record EventListResponse(
        Long totalCount,
        int totalPages,
        int currentPage,
        List<EventSummaryResponse> events
) {
}
