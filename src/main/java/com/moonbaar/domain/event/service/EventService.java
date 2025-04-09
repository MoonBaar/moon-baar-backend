package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.dto.EventSummaryResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.exeption.EventNotFoundException;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.event.repository.EventSpecifications;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final CulturalEventRepository eventRepository;

    public EventListResponse searchEvents(EventSearchRequest request) {
        Specification<CulturalEvent> spec = createSearchSpecification(request);
        Pageable pageable = createPageableWithSort(request);

        Page<CulturalEvent> eventPage = eventRepository.findAll(spec, pageable);
        List<EventSummaryResponse> eventResponses = EventSummaryResponse.fromList(eventPage.getContent());

        return createEventListResponse(eventPage, eventResponses);
    }

    private Specification<CulturalEvent> createSearchSpecification(EventSearchRequest request) {
        return EventSpecifications.withSearchCriteria(
                request.query(),
                request.categoryId(),
                request.districtId(),
                request.startDate(),
                request.endDate(),
                request.isFree()
        );
    }

    private Pageable createPageableWithSort(EventSearchRequest request) {
        Sort sort = createSortCriteria(request.sort(), request.order());
        return PageRequest.of(request.page() - 1, request.size(), sort);
    }

    private Sort createSortCriteria(String sortBy, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        String sortProperty = determineSortProperty(sortBy);
        return Sort.by(direction, sortProperty);
    }

    private String determineSortProperty(String sortBy) {
        if ("popularity".equals(sortBy)) {
            return "id"; // 임시로 id로 정렬(인기도 정렬은 추후 방문수와 좋아요 수 기준으로 확장)
        }
        return "startDate"; // 기본값은 시작일
    }

    private EventListResponse createEventListResponse(Page<CulturalEvent> eventPage, List<EventSummaryResponse> eventResponses) {
        return new EventListResponse(
                eventPage.getTotalElements(),
                eventPage.getTotalPages(),
                eventPage.getNumber() + 1,
                eventResponses
        );
    }

    public EventDetailResponse getEventDetail(Long eventId) {
        CulturalEvent event = findEventById(eventId);
        return EventDetailResponse.from(event);
    }

    private CulturalEvent findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
    }
}
