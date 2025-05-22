package com.moonbaar.domain.event.dto;

public record EventUserStatusResponse(
        Long eventId,
        boolean isVisited,
        boolean isLiked
) {

    public static EventUserStatusResponse of(
            Long eventId,
            boolean isVisited,
            boolean isLiked) {
        return new EventUserStatusResponse(eventId, isVisited, isLiked);
    }
}
