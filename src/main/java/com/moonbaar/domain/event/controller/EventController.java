package com.moonbaar.domain.event.controller;

import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.service.EventService;
import com.moonbaar.domain.like.dto.LikeResponse;
import com.moonbaar.domain.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    // 임시 유저 아이디
    private Long MOCK_USER_ID = 1L;
    private final EventService eventService;
    private final LikeService likeService;

    @GetMapping
    public EventListResponse searchEvents(@ModelAttribute @Valid EventSearchRequest request) {
        return eventService.searchEvents(request);
    }

    @GetMapping("/{eventId}")
    public EventDetailResponse getEventDetail(@PathVariable Long eventId) {
        return eventService.getEventDetail(eventId);
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> likeEvent(@PathVariable Long eventId) {
        LikeResponse response = likeService.likeEvent(MOCK_USER_ID, eventId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<LikeResponse> unlikeEvent(@PathVariable Long eventId) {
        LikeResponse response = likeService.unlikeEvent(MOCK_USER_ID, eventId);
        return ResponseEntity.ok(response);
    }
}
