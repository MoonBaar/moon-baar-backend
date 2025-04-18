package com.moonbaar.domain.visit.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FootprintRequest(
        @NotNull(message = "최소 위도는 필수 입력값입니다")
        BigDecimal minLat,

        @NotNull(message = "최소 경도는 필수 입력값입니다")
        BigDecimal minLng,

        @NotNull(message = "최대 위도는 필수 입력값입니다")
        BigDecimal maxLat,

        @NotNull(message = "최대 경도는 필수 입력값입니다")
        BigDecimal maxLng
) {
}
