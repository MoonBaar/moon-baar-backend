package com.moonbaar.domain.event.dto;

import com.moonbaar.domain.event.entity.CulturalEvent;
import java.util.List;
import org.springframework.data.domain.Page;

public record EventListResponse(
        Long totalCount,
        int totalPages,
        int currentPage,
        List<EventSummaryResponse> events
) {

    public static EventListResponse from(Page<CulturalEvent> eventsPage) {
        return new EventListResponse(
                eventsPage.getTotalElements(),
                eventsPage.getTotalPages(),
                eventsPage.getNumber() + 1,
                EventSummaryResponse.fromList(eventsPage.getContent())
        );
    }

    public static EventListResponse fromWithVisitStatus(
            Page<CulturalEvent> eventsPage,
            List<Long> visitedEventIds) {

        return new EventListResponse(
                eventsPage.getTotalElements(),
                eventsPage.getTotalPages(),
                eventsPage.getNumber() + 1,
                EventSummaryResponse.fromListWithVisitStatus(
                        eventsPage.getContent(),
                        visitedEventIds)
        );
    }
}
