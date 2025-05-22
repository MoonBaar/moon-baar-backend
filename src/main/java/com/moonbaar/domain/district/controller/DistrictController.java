package com.moonbaar.domain.district.controller;

import com.moonbaar.domain.district.dto.DistrictListResponse;
import com.moonbaar.domain.district.dto.DistrictResponse;
import com.moonbaar.domain.district.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    public DistrictListResponse getDistricts() {
        return districtService.getAllDistricts();
    }

    @GetMapping("/by-name/{name}")
    public DistrictResponse getDistrictByName(@PathVariable String name) {
        return districtService.getDistrictByName(name);
    }
}
