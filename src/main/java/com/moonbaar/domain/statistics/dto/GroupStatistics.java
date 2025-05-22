package com.moonbaar.domain.statistics.dto;

import java.util.List;

public record GroupStatistics(
        TopStatistic top,
        List<String> all
) {

    public static GroupStatistics of(TopStatistic top, List<String> all) {
        return new GroupStatistics(top, all);
    }

    public record TopStatistic(
            String name,
            Long count,
            Integer percentage
    ) {
        public static TopStatistic of(String name, long count, int percentage) {
            return new TopStatistic(name, count, percentage);
        }
    }
}
