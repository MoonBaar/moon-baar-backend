package com.moonbaar.domain.statistics.dto;

import java.util.List;

public record GroupStatistics(
        StatisticsItem top,
        List<StatisticsItem> all
) {

    public static GroupStatistics of(
            StatisticsItem top,
            List<StatisticsItem> all
    ) {
        return new GroupStatistics(top, all);
    }
}
