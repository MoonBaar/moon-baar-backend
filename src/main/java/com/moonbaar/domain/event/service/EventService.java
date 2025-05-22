package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.event.repository.CulturalEventSpecifications;
import com.moonbaar.domain.like.repository.LikedEventRepository;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final CulturalEventRepository eventRepository;
    private final EventProvider eventProvider;
    private final LikedEventRepository likedEventRepository;
    private final VisitRepository visitRepository;
    private final CulturalEventRepository culturalEventRepository;

    public EventListResponse searchEvents(EventSearchRequest request) {
        Specification<CulturalEvent> spec = createSearchSpecification(request);
        Page<CulturalEvent> eventPage = eventRepository.findAll(spec, request.toPageable());

        return EventListResponse.from(eventPage);
    }

    public EventListResponse searchEventsWithUserStatus(User user, EventSearchRequest request) {
        Specification<CulturalEvent> spec = createSearchSpecification(request);
        Page<CulturalEvent> eventPage = eventRepository.findAll(spec, request.toPageable());

        List<Long> eventIds = eventPage.getContent().stream()
                .map(CulturalEvent::getId)
                .toList();

        List<Long> visitedEventIds = getVisitedEventIds(user.getId(), eventIds);
        return EventListResponse.fromWithVisitStatus(eventPage, visitedEventIds);
    }

    private List<Long> getVisitedEventIds(Long userId, List<Long> eventIds) {
        if (eventIds.isEmpty()) return List.of();
        return visitRepository.findVisitedEventIdsByUserIdAndEventIdsIn(userId, eventIds);
    }

    private Specification<CulturalEvent> createSearchSpecification(EventSearchRequest request) {
        return CulturalEventSpecifications.withSearchCriteria(
                request.query(),
                request.categoryId(),
                request.districtId(),
                request.startDate(),
                request.endDate(),
                request.isFree()
        );
    }

    public EventDetailResponse getEventDetail(Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);

        long visitCount = visitRepository.countByEventId(eventId);
        long likeCount = likedEventRepository.countByEventId(eventId);

        return EventDetailResponse.of(event, visitCount, likeCount, false, false);
    }

    public EventDetailResponse getEventDetailWithUserStatus(User user, Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);

        long visitCount = visitRepository.countByEventId(eventId);
        long likeCount = likedEventRepository.countByEventId(eventId);

        boolean isVisited = visitRepository.existsByUserAndEvent(user, event);
        boolean isLiked = likedEventRepository.existsByUserAndEvent(user, event);

        return EventDetailResponse.of(event, visitCount, likeCount, isVisited, isLiked);
    }
}
