package com.moonbaar.domain.visit.dto;

import com.moonbaar.domain.visit.entity.Visit;
import java.time.LocalDateTime;

public record VisitResponse(
        Long visitId,
        Long eventId,
        String eventTitle,
        LocalDateTime visitedAt
) {
    public static VisitResponse from(Visit visit) {
        return new VisitResponse(
                visit.getId(),
                visit.getEvent().getId(),
                visit.getEvent().getTitle(),
                visit.getVisitedAt()
        );
    }
}
