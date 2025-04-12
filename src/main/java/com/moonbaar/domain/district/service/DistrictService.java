package com.moonbaar.domain.district.service;

import com.moonbaar.domain.category.dto.CategoryResponse;
import com.moonbaar.domain.category.exception.CategoryNotFoundException;
import com.moonbaar.domain.district.dto.DistrictListResponse;
import com.moonbaar.domain.district.dto.DistrictResponse;
import com.moonbaar.domain.district.exception.DistrictNotFoundException;
import com.moonbaar.domain.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistrictService {

    private final DistrictRepository districtRepository;

    public DistrictListResponse getAllDistricts() {
        return DistrictListResponse.from(districtRepository.findAll());
    }

    public DistrictResponse getDistrictByName(String name) {
        return districtRepository.findByName(name)
                .map(DistrictResponse::from)
                .orElseThrow(DistrictNotFoundException::new);
    }
}
