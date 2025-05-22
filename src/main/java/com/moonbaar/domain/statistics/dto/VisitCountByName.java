package com.moonbaar.domain.statistics.dto;

public record VisitCountByName(
        String name,
        Long count
) {}
