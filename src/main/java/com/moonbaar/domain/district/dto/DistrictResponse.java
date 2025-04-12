package com.moonbaar.domain.district.dto;

import com.moonbaar.domain.district.entity.District;

public record DistrictResponse(
        Long id,
        String name
) {

    public static DistrictResponse from(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getName());
    }
}
