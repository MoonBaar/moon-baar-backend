package com.moonbaar.domain.badge.dto;

import com.moonbaar.domain.badge.entity.Badge;

public record BadgeProgressResponse(
        Long id,
        String code,
        String name,
        String description,
        long progress,
        long target
) {
    public static BadgeProgressResponse of(Badge badge, long progress, long target) {
        return new BadgeProgressResponse(
                badge.getId(),
                badge.getCode().name(),
                badge.getName(),
                badge.getDescription(),
                progress,
                target
        );
    }
}
