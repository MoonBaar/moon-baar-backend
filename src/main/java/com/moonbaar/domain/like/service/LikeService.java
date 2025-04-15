package com.moonbaar.domain.like.service;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.exeption.EventNotFoundException;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.entity.LikedEvent;
import com.moonbaar.domain.like.exception.AlreadyLikedEventException;
import com.moonbaar.domain.like.exception.LikeNotFoundException;
import com.moonbaar.domain.like.repository.LikedEventRepository;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.user.exception.UserNotFoundException;
import com.moonbaar.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final CulturalEventRepository culturalEventRepository;
    private final LikedEventRepository likedEventRepository;

    @Transactional
    public LikeResponse likeEvent(Long userId, Long eventId) {
        User user = findUser(userId);
        CulturalEvent event = findEvent(eventId);

        checkNotAlreadyLiked(user, event);

        LikedEvent likedEvent = LikedEvent.builder()
                .user(user)
                .event(event)
                .build();
        likedEventRepository.save(likedEvent);

        return LikeResponse.of(eventId, true);
    }

    @Transactional
    public LikeResponse unlikeEvent(Long userId, Long eventId) {
        User user = findUser(userId);
        CulturalEvent event = findEvent(eventId);

        LikedEvent likedEvent = findLikedEvent(user, event);
        likedEventRepository.delete(likedEvent);

        return LikeResponse.of(eventId, false);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private CulturalEvent findEvent(Long eventId) {
        return culturalEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
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
}
