package com.moonbaar.domain.badge.dto;

public record BadgeProgressResponse(
        Long id,
        String code,
        String name,
        String description,
        long progress,
        long target
) {
}
