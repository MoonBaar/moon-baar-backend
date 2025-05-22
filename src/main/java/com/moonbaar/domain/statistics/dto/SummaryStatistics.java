package com.moonbaar.domain.statistics.dto;

public record SummaryStatistics(
        long totalVisits,
        long thisMonthVisits
) {

    public static SummaryStatistics of(long totalVisits, long thisMonthVisits) {
        return new SummaryStatistics(totalVisits, thisMonthVisits);
    }
}
