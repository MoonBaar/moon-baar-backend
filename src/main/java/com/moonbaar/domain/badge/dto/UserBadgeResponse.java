package com.moonbaar.domain.badge.dto;

import com.moonbaar.domain.badge.entity.UserBadge;

import java.time.LocalDateTime;

public record UserBadgeResponse(
        Long id,
        Long badgeId,
        String badgeType,
        String code,
        String name,
        String description,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static UserBadgeResponse from(UserBadge userBadge) {
        return new UserBadgeResponse(
                userBadge.getId(),
                userBadge.getBadge().getId(),
                userBadge.getBadge().getBadgeType().name(),
                userBadge.getBadge().getCode().name(),
                userBadge.getBadge().getName(),
                userBadge.getBadge().getDescription(),
                userBadge.getBadge().getImageUrl(),
                userBadge.getCreatedAt()
        );
    }
}
