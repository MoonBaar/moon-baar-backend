package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.dto.EventSummaryResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.event.repository.EventSpecifications;
import java.util.List;
import java.util.Optional;
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
        List<EventSummaryResponse> eventResponses = mapToEventResponses(eventPage.getContent());

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

    private List<EventSummaryResponse> mapToEventResponses(List<CulturalEvent> events) {
        return events.stream()
                .map(event -> mapToEventResponse(event))
                .toList();
    }

    private EventSummaryResponse mapToEventResponse(CulturalEvent event) {
//        boolean isLiked = checkIfEventIsLiked(event.getId(), userId);
        boolean isFreeEvent = "무료".equals(event.getIsFree());

        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                Optional.ofNullable(event.getCategory()).map(c -> c.getName()).orElse(null),
                Optional.ofNullable(event.getDistrict()).map(d -> d.getName()).orElse(null),
                event.getPlace(),
                event.getStartDate(),
                event.getEndDate(),
                isFreeEvent,
                event.getMainImg(),
                event.getLatitude(),
                event.getLongitude()
//                isLiked
        );
    }

//    private boolean checkIfEventIsLiked(Long eventId, Long userId) {
//        if (userId == null) {
//            return false;
//        }
//        return likedEventRepository.existsByEventIdAndUserId(eventId, userId);
//    }

    private EventListResponse createEventListResponse(Page<CulturalEvent> eventPage, List<EventSummaryResponse> eventResponses) {
        return new EventListResponse(
                eventPage.getTotalElements(),
                eventPage.getTotalPages(),
                eventPage.getNumber() + 1,
                eventResponses
        );
    }
}
