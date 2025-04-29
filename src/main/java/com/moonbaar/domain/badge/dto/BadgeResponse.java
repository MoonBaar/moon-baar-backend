package com.moonbaar.domain.badge.dto;

import com.moonbaar.domain.badge.entity.Badge;

public record BadgeResponse(
        Long id,
        String badgeType,
        String code,
        String name,
        String description,
        String imageUrl,
        boolean owned
) {
    public static BadgeResponse of(Badge badge, boolean owned) {
        return new BadgeResponse(
                badge.getId(),
                badge.getBadgeType().name(),
                badge.getCode().name(),
                badge.getName(),
                badge.getDescription(),
                badge.getImageUrl(),
                owned
        );
    }
}