package com.moonbaar.domain.visit.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VisitRequest(
        @NotNull(message = "위도는 필수 입력값입니다")
        BigDecimal latitude,

        @NotNull(message = "경도는 필수 입력값입니다")
        BigDecimal longitude
) {
}
