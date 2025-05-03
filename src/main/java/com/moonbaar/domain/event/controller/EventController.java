package com.moonbaar.domain.event.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventListResponse;
import com.moonbaar.domain.event.dto.EventSearchRequest;
import com.moonbaar.domain.event.service.EventService;
import com.moonbaar.domain.event.dto.EventUserStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping
    public EventListResponse searchEvents(@ModelAttribute @Valid EventSearchRequest request) {
        return eventService.searchEvents(request);
    }

    @GetMapping("/{eventId}")
    public EventDetailResponse getEventDetail(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

            return eventService.getEventDetail(eventId);
    }

    @GetMapping("/{eventId}/user-status")
    public EventUserStatusResponse getEventStatus(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return eventService.getUserEventStatus(userDetails.getUser(), eventId);
    }
}
