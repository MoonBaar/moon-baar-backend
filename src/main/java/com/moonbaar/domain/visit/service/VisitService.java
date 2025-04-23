package com.moonbaar.domain.visit.service;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.service.EventProvider;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.service.UserProvider;
import com.moonbaar.domain.visit.dto.VisitRequest;
import com.moonbaar.domain.visit.dto.VisitResponse;
import com.moonbaar.domain.visit.entity.Visit;
import com.moonbaar.domain.visit.exception.InvalidLocationException;
import com.moonbaar.domain.visit.exception.RecentlyVisitedException;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitService {

    @Value("${moonbaar.visit.max-distance-km}")
    private double MAX_DISTANCE_KM;

    @Value("${moonbaar.visit.revisit-hours-interval}")
    private int REVISIT_HOURS_INTERVAL;

    private final UserProvider userProvider;
    private final EventProvider eventProvider;
    private final VisitRepository visitRepository;

    @Transactional
    public VisitResponse visitEvent(Long userId, Long eventId, VisitRequest request) {
        User user = userProvider.getUserById(userId);
        CulturalEvent event = eventProvider.getEventById(eventId);

        validateVisitRequest(user, event, request);
        Visit visit = createVisit(user, event);

        return VisitResponse.from(visit);
    }

    private Visit createVisit(User user, CulturalEvent event) {
        Visit visit = Visit.builder()
                .user(user)
                .event(event)
                .visitedAt(LocalDateTime.now())
                .build();

        return visitRepository.save(visit);
    }

    private void validateVisitRequest(User user, CulturalEvent event, VisitRequest request) {
        checkNotRecentlyVisited(user, event);
        checkLocationValid(event, request);
    }

    private void checkNotRecentlyVisited(User user, CulturalEvent event) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(REVISIT_HOURS_INTERVAL);

        Optional<Visit> recentVisit = visitRepository.findTopByUserAndEventOrderByVisitedAtDesc(user, event);

        if (recentVisit.isPresent() && recentVisit.get().getVisitedAt().isAfter(cutoffTime)) {
            throw new RecentlyVisitedException();
        }
    }

    private void checkLocationValid(CulturalEvent event, VisitRequest request) {
        double distanceKm = calculateDistance(
                event.getLatitude().doubleValue(),
                event.getLongitude().doubleValue(),
                request.latitude().doubleValue(),
                request.longitude().doubleValue()
        );

        if (distanceKm > MAX_DISTANCE_KM) {
            throw new InvalidLocationException();
        }
    }

    // 하버사인 공식을 사용한 두 지점 간 거리 계산 (km)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
