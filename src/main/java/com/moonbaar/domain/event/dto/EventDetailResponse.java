package com.moonbaar.domain.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDetailResponse(
        Long id,
        String title,
        String category,
        String district,
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isFree,
        String useFee,
        String useTarget,
        String player,
        String program,
        String etcDesc,
        String mainImg,
        String orgName,
        String orgLink,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
