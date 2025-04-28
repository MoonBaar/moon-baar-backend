package com.moonbaar.domain.visit.dto;

import com.moonbaar.domain.visit.entity.Visit;
import java.util.List;
import org.springframework.data.domain.Page;

public record VisitListResponse(
        Long totalCount,
        int totalPages,
        int currentPage,
        List<VisitItemResponse> events
) {

    public static VisitListResponse from(Page<Visit> visitsPage) {
        return new VisitListResponse(
                visitsPage.getTotalElements(),
                visitsPage.getTotalPages(),
                visitsPage.getNumber() + 1,
                VisitItemResponse.fromList(visitsPage.getContent())
        );
    }
}
