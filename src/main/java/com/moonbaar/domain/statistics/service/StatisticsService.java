package com.moonbaar.domain.statistics.service;

import com.moonbaar.domain.statistics.dto.GroupStatistics;
import com.moonbaar.domain.statistics.dto.GroupStatistics.TopStatistic;
import com.moonbaar.domain.statistics.dto.StatisticsResponse;
import com.moonbaar.domain.statistics.dto.SummaryStatistics;
import com.moonbaar.domain.statistics.dto.VisitCountByName;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final VisitRepository visitRepository;

    public StatisticsResponse getUserStatistics(Long userId) {
        long total = visitRepository.countByUserId(userId);
        return StatisticsResponse.of(
                summary(userId, total),
                group("category", userId, total),
                group("district", userId, total)
        );
    }

    private SummaryStatistics summary(Long userId, long total) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthly = visitRepository.countByUserIdAndVisitedAtAfter(userId, startOfMonth);
        return SummaryStatistics.of(total, monthly);
    }

    private GroupStatistics group(String type, Long userId, long total) {
        List<VisitCountByName> counts = switch (type) {
            case "category" -> visitRepository.countVisitsByCategory(userId);
            case "district" -> visitRepository.countVisitsByDistrict(userId);
            default -> List.of();
        };
        return toGroupStatistics(counts, total);
    }

    private GroupStatistics toGroupStatistics(List<VisitCountByName> counts, long total) {
        if (counts.isEmpty()) return GroupStatistics.of(null, List.of());

        return GroupStatistics.of(
                toTopStatistic(counts.get(0), total),
                extractNames(counts)
        );
    }

    private TopStatistic toTopStatistic(VisitCountByName topItem, long total) {
        return TopStatistic.of(
                topItem.name(),
                topItem.count(),
                percentage(topItem.count(), total)
        );
    }

    private List<String> extractNames(List<VisitCountByName> items) {
        return items.stream()
                .map(VisitCountByName::name)
                .toList();
    }

    private int percentage(long count, long total) {
        if (total == 0) return 0;
        return (int) Math.round((double) count * 100 / total);
    }
}
