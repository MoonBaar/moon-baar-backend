package com.moonbaar.domain.visit.dto;

import com.moonbaar.domain.visit.entity.Visit;
import java.util.List;

public record FootprintListResponse(
        List<FootprintResponse> events
) {

    public static FootprintListResponse from(List<Visit> visits) {
        List<FootprintResponse> events = visits.stream()
                .map(FootprintResponse::from)
                .toList();
        return new FootprintListResponse(events);
    }
}
