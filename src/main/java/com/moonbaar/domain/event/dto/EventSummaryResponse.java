package com.moonbaar.domain.event.dto;

import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import com.moonbaar.domain.event.entity.CulturalEvent;
import java.math.BigDecimal;
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
        BigDecimal latitude,
        BigDecimal longitude
) {

    public static EventSummaryResponse from(CulturalEvent event) {
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
                event.getLatitude(),
                event.getLongitude()
        );
    }

    public static List<EventSummaryResponse> fromList(List<CulturalEvent> events) {
        return events.stream()
                .map(EventSummaryResponse::from)
                .toList();
    }
}
