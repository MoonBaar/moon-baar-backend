package com.moonbaar.domain.badge.dto;

import com.moonbaar.domain.badge.entity.UserBadge;

import java.util.List;

public record UserBadgeListResponse(
    List<UserBadgeResponse> userBadges
) {
    public static UserBadgeListResponse from(List<UserBadge> userBadges) {
        List<UserBadgeResponse> userBadgeResponses = userBadges.stream()
                .map(UserBadgeResponse::from)
                .toList();
        return new UserBadgeListResponse(userBadgeResponses);
    }
}
