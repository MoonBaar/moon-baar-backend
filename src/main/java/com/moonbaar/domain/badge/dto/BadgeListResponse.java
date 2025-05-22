package com.moonbaar.domain.badge.dto;

import java.util.List;

public record BadgeListResponse(
        List<BadgeResponse> badges
) {
}
