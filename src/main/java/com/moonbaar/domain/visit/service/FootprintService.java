package com.moonbaar.domain.visit.service;

import com.moonbaar.domain.visit.dto.FootprintListResponse;
import com.moonbaar.domain.visit.dto.FootprintRequest;
import com.moonbaar.domain.visit.entity.Visit;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FootprintService {

    private final VisitRepository visitRepository;

    public FootprintListResponse findUserFootprints(Long userId, FootprintRequest request) {
        List<Visit> visits = visitRepository.findFootprintsByUserIdAndBounds(
                userId,
                request.minLat(),
                request.maxLat(),
                request.minLng(),
                request.maxLng()
        );
        return FootprintListResponse.from(visits);
    }
}
