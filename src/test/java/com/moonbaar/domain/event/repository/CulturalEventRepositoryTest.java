package com.moonbaar.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.moonbaar.domain.event.entity.CulturalEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용
public class CulturalEventRepositoryTest {

    @Autowired
    private CulturalEventRepository eventRepository;

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
}
