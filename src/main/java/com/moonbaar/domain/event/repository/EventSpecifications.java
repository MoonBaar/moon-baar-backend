package com.moonbaar.domain.event.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

    public static Specification<CulturalEvent> withSearchCriteria(
            String query,
            Long categoryId,
            Long districtId,
            LocalDate startDate,
            LocalDate endDate,
            Boolean isFree
    ) {
        return (Root<CulturalEvent> root, CriteriaQuery<?> query1, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addQueryPredicate(query, root, cb, predicates);
            addCategoryPredicate(categoryId, root, cb, predicates);
            addDistrictPredicate(districtId, root, cb, predicates);
            addDatePredicates(startDate, endDate, root, cb, predicates);
            addFreePredicate(isFree, root, cb, predicates);

            // 종료된 행사는 제외
            predicates.add(cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.now())
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addQueryPredicate(
            String query,
            Root<CulturalEvent> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (query != null && !query.trim().isEmpty()) {
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("place")), "%" + query.toLowerCase() + "%")
            ));
        }
    }

    private static void addCategoryPredicate(
            Long categoryId,
            Root<CulturalEvent> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }
    }

    private static void addDistrictPredicate(
            Long districtId,
            Root<CulturalEvent> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (districtId != null) {
            predicates.add(cb.equal(root.get("district").get("id"), districtId));
        }
    }

    private static void addDatePredicates(
            LocalDate startDate,
            LocalDate endDate,
            Root<CulturalEvent> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        // 검색 시작일이 있는 경우: 행사 종료일 >= 검색 시작일 (또는 종료일이 null인 경우)
        if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            predicates.add(cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), startDateTime)
            ));
        }

        // 검색 종료일이 있는 경우: 행사 시작일 <= 검색 종료일
        if (endDate != null) {
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            predicates.add(cb.lessThan(root.get("startDate"), endDateTime));
        }
    }

    private static void addFreePredicate(
            Boolean isFree,
            Root<CulturalEvent> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (isFree != null) {
            String freeValue = isFree ? "무료" : "유료";
            predicates.add(cb.equal(root.get("isFree"), freeValue));
        }
    }
}
