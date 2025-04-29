package com.moonbaar.domain.visit.dto;

import com.moonbaar.domain.visit.entity.Visit;
import java.time.LocalDateTime;
import java.util.List;

public record VisitItemResponse(
        Long id,
        Long eventId,
        String title,
        String place,
        String mainImg,
        LocalDateTime visitedAt
) {

    public static VisitItemResponse from(Visit visit) {
        return new VisitItemResponse(
                visit.getId(),
                visit.getEvent().getId(),
                visit.getEvent().getTitle(),
                visit.getEvent().getPlace(),
                visit.getEvent().getMainImg(),
                visit.getVisitedAt()
        );
    }

    public static List<VisitItemResponse> fromList(List<Visit> visits) {
        return visits.stream()
                .map(VisitItemResponse::from)
                .toList();
    }
}
