package com.moonbaar.domain.badge.dto;

import java.util.Map;

/**
 * 배지 획득 여부 판단에 필요한 데이터를 모아둔 클래스
 * @param totalVisitCount 행사 방문 횟수
 * @param visitedDistrictCount  방문한 구 개수
 * @param visitCountByCategory  카테고리별 방문 횟수
 */
public record BadgeEvaluationContext(
    long totalVisitCount,
    int visitedDistrictCount,
    Map<String, Long> visitCountByCategory
){
    /**
     * @param categoryName  카테고리명
     * @return  카테고리명에 해당하는 행사에 방문한 횟수
     */
    public long getVisitCountByCategoryName(String categoryName) {
        return visitCountByCategory.getOrDefault(categoryName, 0L);
    }

    /**
     * @param prefix 카테고리명의 시작 단어
     * @return prefix로 카테고리명이 시작하는 행사에 방문한 횟수
     */
    public long getVisitCountByCategoryPrefix(String prefix) {
        return visitCountByCategory.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .mapToLong(Map.Entry::getValue)
                .sum();
    }
}
