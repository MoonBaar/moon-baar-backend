package com.moonbaar.domain.event.dto;

import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import com.moonbaar.domain.event.entity.CulturalEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record EventSummaryResponse(
        Long id,
        String title,
        String category,
        String district,
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isFree,
        String mainImg,
        boolean isVisited
) {

    public static EventSummaryResponse from(CulturalEvent event) {
        return fromWithVisitStatus(event, false);
    }

    public static EventSummaryResponse fromWithVisitStatus(CulturalEvent event, boolean isVisited) {
        boolean isFreeEvent = "무료".equals(event.getIsFree());

        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                Optional.ofNullable(event.getCategory()).map(Category::getName).orElse(null),
                Optional.ofNullable(event.getDistrict()).map(District::getName).orElse(null),
                event.getPlace(),
                event.getStartDate(),
                event.getEndDate(),
                isFreeEvent,
                event.getMainImg(),
                isVisited
        );
    }

    public static List<EventSummaryResponse> fromList(List<CulturalEvent> events) {
        return events.stream()
                .map(EventSummaryResponse::from)
                .toList();
    }

    public static List<EventSummaryResponse> fromListWithVisitStatus(
            List<CulturalEvent> events,
            List<Long> visitedEventIds) {

        return events.stream()
                .map(event -> fromWithVisitStatus(
                        event,
                        visitedEventIds.contains(event.getId())))
                .toList();
    }
}
