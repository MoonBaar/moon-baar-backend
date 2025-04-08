package com.moonbaar.domain.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSummaryResponse(
        Long id,
        String title,
        String category,
        String district,
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isFree,
        String imageUrl,
        BigDecimal latitude,
        BigDecimal longitude
//        boolean isLiked
) {
}
