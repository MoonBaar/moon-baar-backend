package com.moonbaar.domain.statistics.service;

import com.moonbaar.domain.statistics.dto.GroupStatistics;
import com.moonbaar.domain.statistics.dto.StatisticsItem;
import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.dto.SummaryStatistics;
import com.moonbaar.domain.statistics.dto.VisitCountByName;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final VisitRepository visitRepository;

    public StatisticsResponse getUserStatistics(Long userId) {
        long totalVisits = visitRepository.countByUserId(userId);

        SummaryStatistics summary = getSummaryStatistics(userId, totalVisits);

        List<VisitCountByName> categoryVisits = visitRepository.countVisitsByCategory(userId);
        GroupStatistics categories = processVisitCounts(categoryVisits, userId, totalVisits);

        List<VisitCountByName> districtVisits = visitRepository.countVisitsByDistrict(userId);
        GroupStatistics districts = processVisitCounts(districtVisits, userId, totalVisits);

        return StatisticsResponse.of(summary, categories, districts);
    }

    private SummaryStatistics getSummaryStatistics(Long userId, long totalVisits) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long thisMonthVisits = visitRepository.countByUserIdAndVisitedAtAfter(userId, startOfMonth);

        return SummaryStatistics.of(totalVisits, thisMonthVisits);
    }

    private GroupStatistics processVisitCounts(List<VisitCountByName> visitCounts, Long userId, long totalVisits) {
        if (visitCounts.isEmpty()) {
            return GroupStatistics.of(null, List.of());
        }

        StatisticsItem topItem = createTopItem(visitCounts.get(0), totalVisits);
        List<StatisticsItem> allItems = createAllItems(visitCounts, totalVisits);

        return GroupStatistics.of(topItem, allItems);
    }

    private StatisticsItem createTopItem(VisitCountByName topVisit, long totalVisits) {
        int topPercentage = calculatePercentage(topVisit.count(), totalVisits);
        return StatisticsItem.of(
                topVisit.name(),
                topVisit.count(),
                topPercentage
        );
    }

    private List<StatisticsItem> createAllItems(List<VisitCountByName> visitCounts, long totalVisits) {
        return visitCounts.stream()
                .map(visit -> {
                    int percentage = calculatePercentage(visit.count(), totalVisits);
                    return StatisticsItem.of(visit.name(), visit.count(), percentage);
                })
                .collect(Collectors.toList());
    }

    private int calculatePercentage(long count, long total) {
        if (total == 0) return 0;
        return (int) Math.round((double) count / total * 100);
    }
}
