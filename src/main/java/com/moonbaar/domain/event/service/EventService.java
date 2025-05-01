package com.moonbaar.domain.event.service;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.dto.EventSummaryResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.event.repository.CulturalEventSpecifications;
import com.moonbaar.domain.like.repository.LikedEventRepository;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.repository.VisitRepository;
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

    private final CulturalEventRepository eventRepository;
    private final EventProvider eventProvider;
    private final LikedEventRepository likedEventRepository;
    private final VisitRepository visitRepository;

    public EventListResponse searchEvents(EventSearchRequest request) {
        Specification<CulturalEvent> spec = createSearchSpecification(request);
        Pageable pageable = request.toPageable();

        Page<CulturalEvent> eventPage = eventRepository.findAll(spec, pageable);
        List<EventSummaryResponse> eventResponses = EventSummaryResponse.fromList(eventPage.getContent());

        return createEventListResponse(eventPage, eventResponses);
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
        long visitCount = visitRepository.countByEventId(eventId);
        long likeCount = likedEventRepository.countByEventId(eventId);

        return EventDetailResponse.of(event, false, false, visitCount, likeCount);
    }

    public EventDetailResponse getEventDetailForUser(User user, Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);
        long visitCount = visitRepository.countByEventId(eventId);
        long likeCount = likedEventRepository.countByEventId(eventId);

        boolean isVisited = checkIfEventIsVisited(user, event);
        boolean isLiked = checkIfEventIsLiked(user, event);
        return EventDetailResponse.of(event, isVisited, isLiked, visitCount, likeCount);
    }

    private boolean checkIfEventIsLiked(User user, CulturalEvent event) {
        if (user == null) {
            return false;
        }
        return likedEventRepository.existsByUserAndEvent(user, event);
    }

    private boolean checkIfEventIsVisited(User user, CulturalEvent event) {
        if (user == null) {
            return false;
        }
        return visitRepository.existsByUserAndEvent(user, event);
    }
}
