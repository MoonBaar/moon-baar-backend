package com.moonbaar.domain.like.service;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.service.EventProvider;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.dto.LikedEventListRequest;
import com.moonbaar.domain.like.dto.LikedEventListResponse;
import com.moonbaar.domain.like.entity.LikedEvent;
import com.moonbaar.domain.like.exception.AlreadyLikedEventException;
import com.moonbaar.domain.like.exception.LikeNotFoundException;
import com.moonbaar.domain.like.repository.LikedEventRepository;
import com.moonbaar.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final EventProvider eventProvider;
    private final LikedEventRepository likedEventRepository;

    @Transactional
    public LikeResponse likeEvent(User user, Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);

        checkNotAlreadyLiked(user, event);

        LikedEvent likedEvent = LikedEvent.builder()
                .user(user)
                .event(event)
                .build();
        likedEventRepository.save(likedEvent);

        return LikeResponse.of(eventId, true);
    }

    @Transactional
    public LikeResponse unlikeEvent(User user, Long eventId) {
        CulturalEvent event = eventProvider.getEventById(eventId);

        LikedEvent likedEvent = findLikedEvent(user, event);
        likedEventRepository.delete(likedEvent);

        return LikeResponse.of(eventId, false);
    }

    private void checkNotAlreadyLiked(User user, CulturalEvent event) {
        if (likedEventRepository.existsByUserAndEvent(user, event)) {
            throw new AlreadyLikedEventException();
        }
    }

    private LikedEvent findLikedEvent(User user, CulturalEvent event) {
        return likedEventRepository.findByUserAndEvent(user, event)
                .orElseThrow(LikeNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public LikedEventListResponse getLikedEvents(User user, LikedEventListRequest request) {
        Pageable pageable = request.toPageable();

        Page<LikedEvent> likedEventsPage = likedEventRepository.findByUser(user, pageable);
        return LikedEventListResponse.from(likedEventsPage);
    }
}
