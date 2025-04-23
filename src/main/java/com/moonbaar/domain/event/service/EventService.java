package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.dto.EventSummaryResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.repository.EventRepository;
import com.moonbaar.domain.event.repository.EventSpecifications;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventProvider eventProvider;

    public EventListResponse searchEvents(EventSearchRequest request) {
        Specification<CulturalEvent> spec = createSearchSpecification(request);
        Pageable pageable = request.toPageable();

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

    private EventListResponse createEventListResponse(Page<CulturalEvent> eventPage, List<EventSummaryResponse> eventResponses) {
        return new EventListResponse(
                eventPage.getTotalElements(),
                eventPage.getTotalPages(),
                eventPage.getNumber() + 1,
                eventResponses
        );
    }

    public EventDetailResponse getEventDetail(Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);
        return EventDetailResponse.from(event);
    }
}
