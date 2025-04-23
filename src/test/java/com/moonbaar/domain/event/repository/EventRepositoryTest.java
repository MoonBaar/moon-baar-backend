package com.moonbaar.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.moonbaar.domain.event.entity.CulturalEvent;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testFindAll() {
        // 기본 findAll 테스트
        Page<CulturalEvent> events = eventRepository.findAll(PageRequest.of(0, 10));

        // 결과 출력
        System.out.println("Total events: " + events.getTotalElements());
        events.getContent().forEach(event -> {
            System.out.println("Event ID: " + event.getId());
            System.out.println("Title: " + event.getTitle());
            System.out.println("Start Date: " + event.getStartDate());
            System.out.println("End Date: " + event.getEndDate());
            System.out.println("-------------------");
        });

        // 기본 검증
        assertThat(events).isNotNull();
    }

    @Test
    public void testImprovedDateSearchImplementation() {
        // 테스트할 날짜 범위 설정 (현재 날짜 기준으로 최근 기간 사용)
        LocalDate startDate = LocalDate.now(); // 오늘부터
        LocalDate endDate = startDate.plusDays(5); // 5일 후까지

        // 개선된 구현으로 검색
        Specification<CulturalEvent> improvedSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            LocalDateTime startDateTime = startDate.atStartOfDay();
            predicates.add(cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), startDateTime)
            ));

            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            predicates.add(cb.lessThan(root.get("startDate"), endDateTime));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 검색 실행
        Page<CulturalEvent> events = eventRepository.findAll(improvedSpec, PageRequest.of(0, 30));

        // 결과 출력
        System.out.println("===== 개선된 날짜 검색 결과 =====");
        System.out.println("검색 기간: " + startDate + " ~ " + endDate);
        System.out.println("총 행사 수: " + events.getTotalElements());

        // 각 행사 정보 출력 및 검증
        events.getContent().forEach(event -> {
            System.out.println("Event ID: " + event.getId());
            System.out.println("Title: " + event.getTitle());
            System.out.println("Start Date: " + event.getStartDate());
            System.out.println("End Date: " + event.getEndDate());

            // 각 행사가 검색 기간과 겹치는지 확인
            LocalDateTime eventStartDate = event.getStartDate();
            LocalDateTime eventEndDate = event.getEndDate();

            boolean overlapsWithRange =
                    // 이벤트 종료일이 null이거나 검색 시작일 이후
                    (eventEndDate == null || !eventEndDate.isBefore(startDate.atStartOfDay())) &&
                            // 이벤트 시작일이 검색 종료일 이전
                            eventStartDate.isBefore(endDate.plusDays(1).atStartOfDay());

            System.out.println("기간 겹침 여부: " + overlapsWithRange);
            assertThat(overlapsWithRange).isTrue();
            System.out.println("-------------------");
        });

        // 요구사항 만족 여부 확인
        assertThat(events).isNotNull();
    }
}
