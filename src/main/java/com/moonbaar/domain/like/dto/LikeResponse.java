package com.moonbaar.domain.like.dto;

public record LikeResponse(
        Long eventId,
        boolean isLiked
) {

    public static LikeResponse of(Long eventId, boolean isLiked) {
        return new LikeResponse(eventId, isLiked);
    }
}
