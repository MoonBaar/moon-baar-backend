package com.moonbaar.domain.statistics.dto;

public record StatisticsResponse(
        SummaryStatistics summary,
        GroupStatistics categories,
        GroupStatistics districts
) {

    public static StatisticsResponse of(
            SummaryStatistics summary,
            GroupStatistics categories,
            GroupStatistics districts
    ) {
        return new StatisticsResponse(summary, categories, districts);
    }
}
