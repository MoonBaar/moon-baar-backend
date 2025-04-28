package com.moonbaar.domain.visit.service;

import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.dto.VisitListRequest;
import com.moonbaar.domain.visit.dto.VisitListResponse;
import com.moonbaar.domain.visit.entity.Visit;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserVisitService {

    private final VisitRepository visitRepository;

    public VisitListResponse getUserVisits(User user, VisitListRequest request) {
        Pageable pageable = request.toPageable();

        LocalDateTime startDateTime = determineStartDateTime(request.period());

        Page<Visit> visitsPage = visitRepository.findByUserAndVisitedAtAfter(
                user, startDateTime, pageable);

        return VisitListResponse.from(visitsPage);
    }

    private LocalDateTime determineStartDateTime(String period) {
        LocalDateTime now = LocalDateTime.now();

        return switch(period) {
            case "thisMonth" -> now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            case "thisYear" -> now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
            default -> LocalDateTime.MIN; // "all" 인 경우 가장 과거 날짜 반환
        };
    }
}
