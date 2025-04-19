package com.moonbaar.domain.visit.dto;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.visit.entity.Visit;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FootprintResponse(
        Long id,
        String title,
        String place,
        String mainImg,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime visitedAt
) {

    public static FootprintResponse from(Visit visit) {
        CulturalEvent event = visit.getEvent();

        return new FootprintResponse(
                visit.getId(),
                event.getTitle(),
                event.getPlace(),
                event.getMainImg(),
                event.getLatitude(),
                event.getLongitude(),
                visit.getVisitedAt()
        );
    }
}
