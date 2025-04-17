package com.moonbaar.domain.like.dto;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.like.entity.LikedEvent;
import java.time.LocalDateTime;
import java.util.List;

public record LikedEventResponse(
        Long id,
        String title,
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String mainImg,
        LocalDateTime likedAt
) {

    public static LikedEventResponse from(LikedEvent likedEvent) {
        CulturalEvent event = likedEvent.getEvent();
        return new LikedEventResponse(
                event.getId(),
                event.getTitle(),
                event.getPlace(),
                event.getStartDate(),
                event.getEndDate(),
                event.getMainImg(),
                likedEvent.getCreatedAt()
        );
    }

    public static List<LikedEventResponse> fromList(List<LikedEvent> likedEvents) {
        return likedEvents.stream()
                .map(LikedEventResponse::from)
                .toList();
    }
}
