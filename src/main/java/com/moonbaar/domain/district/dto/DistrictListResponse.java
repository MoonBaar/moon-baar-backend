package com.moonbaar.domain.district.dto;

import com.moonbaar.domain.district.entity.District;
import java.util.List;

public record DistrictListResponse(
        List<DistrictResponse> districts
) {
    public static DistrictListResponse from(List<District> districts) {
        List<DistrictResponse> responses = districts.stream()
                .map(DistrictResponse::from)
                .toList();

        return new DistrictListResponse(responses);
    }
}
