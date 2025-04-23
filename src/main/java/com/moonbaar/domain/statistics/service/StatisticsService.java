package com.moonbaar.domain.statistics.service;

import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.dto.SummaryStatistics;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final VisitRepository visitRepository;

    public StatisticsResponse getUserStatistics(Long userId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        SummaryStatistics summary = getSummaryStatistics(userId, startOfMonth);

        return StatisticsResponse.of(summary, null, null);
    }

    private SummaryStatistics getSummaryStatistics(Long userId, LocalDateTime startOfMonth) {
        long totalVisits = visitRepository.countByUserId(userId);
        long thisMonthVisits = visitRepository.countByUserIdAndVisitedAtAfter(userId, startOfMonth);

        return SummaryStatistics.of(totalVisits, thisMonthVisits);
    }
}
