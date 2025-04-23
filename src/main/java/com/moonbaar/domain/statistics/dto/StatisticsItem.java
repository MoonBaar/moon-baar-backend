package com.moonbaar.domain.statistics.dto;

public record StatisticsItem(
        String name,
        Long count,
        Integer percentage
) {

    public static StatisticsItem of(String name) {
        return new StatisticsItem(name, null, null);
    }

    public static StatisticsItem of(String name, long count, int percentage) {
        return new StatisticsItem(name, count, percentage);
    }
}
